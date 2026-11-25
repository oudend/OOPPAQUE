package se.chalmers.group7.core.ui.model;

import static com.raylib.Raylib.BeginTextureMode;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.DrawCircleLines;
import static com.raylib.Raylib.DrawCircleV;
import static com.raylib.Raylib.DrawRing;
import static com.raylib.Raylib.DrawRingLines;
import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.DrawTexturePro;
import static com.raylib.Raylib.EndTextureMode;
import static com.raylib.Raylib.GetColor;
import static com.raylib.Raylib.GetFrameTime;
import static com.raylib.Raylib.GetMousePosition;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.IsMouseButtonPressed;
import static com.raylib.Raylib.LoadRenderTexture;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;
import static com.raylib.Raylib.MeasureText;
import static com.raylib.Raylib.SetTextureFilter;
import static com.raylib.Raylib.TEXTURE_FILTER_BILINEAR;
import static com.raylib.Raylib.UnloadRenderTexture;

import com.raylib.Raylib.Color;
import com.raylib.Raylib.Rectangle;
import com.raylib.Raylib.RenderTexture;
import com.raylib.Raylib.Vector2;

class RadialColorPicker {

  private enum Drag {
    NONE,
    WHEEL,
    VALUE
  }

  // Gauge sweeps 270 degrees clockwise from bottom-left over the top to
  // bottom-right.
  // Raylib angles: 0 = right, increasing clockwise on screen (y points down).
  private static final float GAUGE_START = 135f;
  private static final float GAUGE_SWEEP = 270f;

  private float cx, cy, radius;
  private float hue = 0f; // 0..360
  private float sat = 1f; // 0..1
  private float val = 1f; // 0..1
  private Drag drag = Drag.NONE;
  private float grab = 0f; // 0..1 animation for handle size

  private RenderTexture wheelTex, gaugeTex;
  private int texSize;
  private float bakedRadius = -1;

  public RadialColorPicker(float cx, float cy, float radius) {
    this.cx = cx;
    this.cy = cy;
    this.radius = radius;
  }

  // ---------- public API ----------

  public void setPosition(float cx, float cy) {
    this.cx = cx;
    this.cy = cy;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public Color getColor() {
    int[] c = hsvToRgb(hue, sat, val);
    return col(c[0], c[1], c[2], 255);
  }

  public String getHex() {
    int[] c = hsvToRgb(hue, sat, val);
    return String.format("#%02X%02X%02X", c[0], c[1], c[2]);
  }

  public void setColor(int r, int g, int b) {
    float rf = r / 255f, gf = g / 255f, bf = b / 255f;
    float max = Math.max(rf, Math.max(gf, bf));
    float min = Math.min(rf, Math.min(gf, bf));
    float d = max - min;
    float h;
    if (d == 0) h = hue; // grey: keep current hue
    else if (max == rf) h = 60f * (((gf - bf) / d) % 6f);
    else if (max == gf) h = 60f * (((bf - rf) / d) + 2f);
    else h = 60f * (((rf - gf) / d) + 4f);
    if (h < 0) h += 360f;
    hue = h;
    sat = max == 0 ? 0 : d / max;
    val = max;
  }

  /** Call once per frame between BeginDrawing/EndDrawing. Returns true if the color changed. */
  public boolean draw() {
    boolean changed = handleInput();
    render();
    return changed;
  }

  /** True if the mouse is over the picker (use this to stop the canvas from painting). */
  public boolean isMouseOver() {
    Vector2 m = GetMousePosition();
    float dx = m.x() - cx, dy = m.y() - cy;
    return Math.sqrt(dx * dx + dy * dy) <= radius + 8 || drag != Drag.NONE;
  }

  // ---------- geometry ----------

  private float wheelRadius() {
    return radius * 0.72f;
  }

  private float gaugeInner() {
    return radius * 0.82f;
  }

  private float gaugeOuter() {
    return radius;
  }

  private float gaugeMid() {
    return (gaugeInner() + gaugeOuter()) * 0.5f;
  }

  private static float angleOf(float dx, float dy) {
    double a = Math.toDegrees(Math.atan2(dy, dx));
    if (a < 0) a += 360;
    return (float) a;
  }

  private float valueFromAngle(float angle) {
    float a = angle;
    if (a < GAUGE_START) a += 360f;
    float end = GAUGE_START + GAUGE_SWEEP; // 405
    if (a > end) { // inside the bottom gap: snap to nearest end
      float mid = end + (360f - GAUGE_SWEEP) / 2f; // 450
      a = a < mid ? end : GAUGE_START;
    }
    return clamp01((a - GAUGE_START) / GAUGE_SWEEP);
  }

  // ---------- input ----------

  private boolean handleInput() {
    Vector2 m = GetMousePosition();
    float dx = m.x() - cx, dy = m.y() - cy;
    float dist = (float) Math.sqrt(dx * dx + dy * dy);
    float ang = angleOf(dx, dy);

    if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
      if (dist <= wheelRadius() + 4) {
        drag = Drag.WHEEL;
      } else if (dist >= gaugeInner() - 4 && dist <= gaugeOuter() + 4) {
        float a = ang < GAUGE_START ? ang + 360f : ang;
        if (a <= GAUGE_START + GAUGE_SWEEP + 6f) drag = Drag.VALUE;
      }
    }
    if (!IsMouseButtonDown(MOUSE_BUTTON_LEFT)) drag = Drag.NONE;

    boolean changed = false;
    if (drag == Drag.WHEEL) {
      float newSat = clamp01(dist / wheelRadius());
      if (newSat != sat || ang != hue) changed = true;
      hue = ang;
      sat = newSat;
    } else if (drag == Drag.VALUE) {
      float newVal = valueFromAngle(ang);
      if (newVal != val) changed = true;
      val = newVal;
    }

    // handle grow/shrink animation
    float target = drag == Drag.NONE ? 0f : 1f;
    grab += (target - grab) * Math.min(1f, GetFrameTime() * 18f);
    return changed;
  }

  // ---------- rendering ----------

  private void render() {
    Vector2 center = new Vector2().x(cx).y(cy);

    // backing panel
    DrawCircleV(center, radius + 10, col(28, 28, 32, 255));
    DrawCircleLines((int) cx, (int) cy, radius + 10, col(70, 70, 80, 255));

    if (bakedRadius != radius) bake();

    int v = Math.round(val * 255f);
    drawBaked(wheelTex, col(v, v, v, 255));

    int[] full = hsvToRgb(hue, sat, 1f);
    drawBaked(gaugeTex, col(full[0], full[1], full[2], 255));
    drawWheelHandle();
    drawGaugeHandle();
    drawPreview();
  }

  private void drawWheel(Vector2 center) {
    final int rings = 18; // saturation steps
    final int slices = 90; // hue steps (4 degrees each)
    float wr = wheelRadius();
    float step = 360f / slices;
    for (int i = 0; i < rings; i++) {
      float inner = wr * i / rings;
      float outer = wr * (i + 1) / rings + 0.75f; // tiny overlap hides seams
      float s = (i + 0.5f) / rings;
      for (int j = 0; j < slices; j++) {
        int[] c = hsvToRgb(j * step + step / 2f, s, val);
        float a0 = j * step;
        DrawRing(center, inner, outer, a0, a0 + step + 0.8f, 2, col(c[0], c[1], c[2], 255));
      }
    }
    DrawRingLines(center, wr, wr + 1.5f, 0, 360, 64, col(15, 15, 18, 255));
  }

  private void drawGauge(Vector2 center) {
    final int slices = 90;
    float step = GAUGE_SWEEP / slices;
    for (int j = 0; j < slices; j++) {
      float t = (j + 0.5f) / slices;
      int[] c = hsvToRgb(hue, sat, t);
      float a0 = GAUGE_START + j * step;
      DrawRing(
          center, gaugeInner(), gaugeOuter(), a0, a0 + step + 0.6f, 2, col(c[0], c[1], c[2], 255));
    }
  }

  private void drawWheelHandle() {
    float rad = (float) Math.toRadians(hue);
    float hx = cx + (float) Math.cos(rad) * sat * wheelRadius();
    float hy = cy + (float) Math.sin(rad) * sat * wheelRadius();
    float r = 8f + 4f * grab;
    Color c = getColor();
    DrawCircleV(new Vector2().x(hx).y(hy), r + 2, col(0, 0, 0, 160));
    DrawCircleV(new Vector2().x(hx).y(hy), r, col(255, 255, 255, 255));
    DrawCircleV(new Vector2().x(hx).y(hy), r - 2.5f, c);
  }

  private void drawGaugeHandle() {
    float ang = (float) Math.toRadians(GAUGE_START + GAUGE_SWEEP * val);
    float hx = cx + (float) Math.cos(ang) * gaugeMid();
    float hy = cy + (float) Math.sin(ang) * gaugeMid();
    float r = (gaugeOuter() - gaugeInner()) * 0.5f + 2f + 2f * grab;
    DrawCircleV(new Vector2().x(hx).y(hy), r + 2, col(0, 0, 0, 160));
    DrawCircleV(new Vector2().x(hx).y(hy), r, col(255, 255, 255, 255));
    DrawCircleV(new Vector2().x(hx).y(hy), r - 2.5f, getColor());
  }

  private void drawPreview() {
    float py = cy + gaugeMid();
    float pr = radius * 0.085f;
    DrawCircleV(new Vector2().x(cx).y(py), pr + 2, col(255, 255, 255, 255));
    DrawCircleV(new Vector2().x(cx).y(py), pr, getColor());

    String hex = getHex();
    int fs = Math.max(12, (int) (radius * 0.12f));
    DrawText(
        hex,
        (int) (cx - MeasureText(hex, fs) / 2f),
        (int) (cy + radius + 20),
        fs,
        col(230, 230, 235, 255));
  }

  // ---------- helpers ----------

  private static Color col(int r, int g, int b, int a) {
    return GetColor((r << 24) | (g << 16) | (b << 8) | a);
  }

  private static float clamp01(float v) {
    return v < 0 ? 0 : (v > 1 ? 1 : v);
  }

  /** h in degrees 0..360, s and v in 0..1. Returns {r,g,b} 0..255. */
  private static int[] hsvToRgb(float h, float s, float v) {
    h = ((h % 360f) + 360f) % 360f;
    float c = v * s;
    float x = c * (1f - Math.abs((h / 60f) % 2f - 1f));
    float m = v - c;
    float r, g, b;
    if (h < 60) {
      r = c;
      g = x;
      b = 0;
    } else if (h < 120) {
      r = x;
      g = c;
      b = 0;
    } else if (h < 180) {
      r = 0;
      g = c;
      b = x;
    } else if (h < 240) {
      r = 0;
      g = x;
      b = c;
    } else if (h < 300) {
      r = x;
      g = 0;
      b = c;
    } else {
      r = c;
      g = 0;
      b = x;
    }
    return new int[] {
      Math.round((r + m) * 255f), Math.round((g + m) * 255f), Math.round((b + m) * 255f)
    };
  }

  private void bake() {
    if (wheelTex != null) {
      UnloadRenderTexture(wheelTex);
      UnloadRenderTexture(gaugeTex);
    }
    texSize = (int) Math.ceil(radius * 2 + 4);
    float c = texSize / 2f;
    Vector2 center = new Vector2().x(c).y(c);

    // wheel at full brightness
    wheelTex = LoadRenderTexture(texSize, texSize);
    BeginTextureMode(wheelTex);
    ClearBackground(col(0, 0, 0, 0));
    final int rings = 36, slices = 180;
    float wr = wheelRadius(), step = 360f / slices;
    for (int i = 0; i < rings; i++) {
      float inner = wr * i / rings;
      float outer = wr * (i + 1) / rings + 0.75f;
      float s = (i + 0.5f) / rings;
      for (int j = 0; j < slices; j++) {
        int[] rgb = hsvToRgb(j * step + step / 2f, s, 1f);
        float a0 = j * step;
        DrawRing(center, inner, outer, a0, a0 + step + 0.8f, 2, col(rgb[0], rgb[1], rgb[2], 255));
      }
    }
    EndTextureMode();

    // gauge as a grey gradient (tinted later)
    gaugeTex = LoadRenderTexture(texSize, texSize);
    BeginTextureMode(gaugeTex);
    ClearBackground(col(0, 0, 0, 0));
    final int gs = 180;
    float gstep = GAUGE_SWEEP / gs;
    for (int j = 0; j < gs; j++) {
      int g = Math.round(255f * (j + 0.5f) / gs);
      float a0 = GAUGE_START + j * gstep;
      DrawRing(center, gaugeInner(), gaugeOuter(), a0, a0 + gstep + 0.6f, 2, col(g, g, g, 255));
    }
    EndTextureMode();

    SetTextureFilter(wheelTex.texture(), TEXTURE_FILTER_BILINEAR);
    SetTextureFilter(gaugeTex.texture(), TEXTURE_FILTER_BILINEAR);
    bakedRadius = radius;
  }

  private void drawBaked(RenderTexture rt, Color tint) {
    // render textures are upside down, so the source height is negative
    Rectangle src = new Rectangle().x(0).y(0).width(texSize).height(-texSize);
    Rectangle dst =
        new Rectangle().x(cx - texSize / 2f).y(cy - texSize / 2f).width(texSize).height(texSize);
    DrawTexturePro(rt.texture(), src, dst, new Vector2().x(0).y(0), 0f, tint);
  }
}
