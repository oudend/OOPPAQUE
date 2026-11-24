package se.chalmers.group7.core.canvas;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import static se.chalmers.group7.core.canvas.utils.RaylibBlendModes.applyBlendMode;

public class RaylibCanvas implements Canvas {
  RenderTexture renderTexture;

  private final int width;
  private final int height;

  public RaylibCanvas(int width, int height) {
    this.width = width;
    this.height = height;

    renderTexture = LoadRenderTexture(this.width, this.height);
  }

  public static RaylibCanvas fromTexture(Texture texture) {
    RaylibCanvas canvas = new RaylibCanvas(texture.width(), texture.height());
    canvas.drawTexture(texture, 0, 0, texture.width(), texture.height(), BlendingMode.REPLACE);

    return canvas;
  }

  @Override
  public Texture getTexture() {
    return renderTexture.texture();
  }

  @Override
  public Canvas clone() {
    Canvas clone = new RaylibCanvas(this.width, this.height);
    clone.drawTexture(renderTexture.texture(), 0, 0, this.width, this.height, BlendingMode.REPLACE);
    return clone;
  }

  @Override
  public void dispose() {
    renderTexture.deallocate(); // maybe causing weird black screen bug?
  }

  @Override
  public void clear() {
    BeginTextureMode(renderTexture);
    ClearBackground(BLANK);
    EndTextureMode();
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  //  private static void applyBlendMode(BlendingMode blendingMode) {
  //    switch (blendingMode) {
  //      case NORMAL:
  //        BeginBlendMode(BLEND_ALPHA);
  //        break;
  //      case REPLACE:
  //        // 1 * src + 0 * dst = exact copy of src (RGB and Alpha)
  //        rlSetBlendFactors(RL_ONE, RL_ZERO, RL_FUNC_ADD);
  //        BeginBlendMode(BLEND_CUSTOM);
  //        break;
  //      case LINEAR_SUBTRACTIVE_ERASE:
  //        rlSetBlendFactorsSeparate(
  //            RL_ZERO, RL_ONE, // RGB: keep destination RGB unchanged
  //            RL_ONE, RL_ONE, // Alpha: 1 * dstAlpha - 1 * srcAlpha
  //            RL_FUNC_ADD, RL_FUNC_REVERSE_SUBTRACT);
  //        BeginBlendMode(BLEND_CUSTOM_SEPARATE);
  //        break;
  //    }
  //  }

  @Override
  public void drawCircle(int x, int y, float radius, Color color, BlendingMode blendingMode) {
    BeginTextureMode(renderTexture);
    applyBlendMode(blendingMode);
    DrawCircleV(new Vector2().x(x).y(y), radius, color);
    EndBlendMode();
    EndTextureMode();
  }

  @Override
  public void drawLine(
      int startX,
      int startY,
      int endX,
      int endY,
      float diameter,
      Color color,
      BlendingMode blendingMode) {
    BeginTextureMode(renderTexture);
    applyBlendMode(blendingMode);
    DrawLineEx(new Vector2().x(startX).y(startY), new Vector2().x(endX).y(endY), diameter, color);
    EndBlendMode();
    EndTextureMode();
  }

  @Override
  public void drawTexture(
      Texture texture, float x, float y, float width, float height, BlendingMode blendingMode) {
    BeginTextureMode(renderTexture);
    applyBlendMode(blendingMode);
    DrawTexturePro(
        texture,
        new Rectangle().x(0).y(0).width(texture.width()).height(-texture.height()),
        new Rectangle().x(x).y(y).width(width).height(height),
        new Vector2().x(0).y(0),
        0,
        WHITE);
    EndBlendMode();
    EndTextureMode();
  }
}
