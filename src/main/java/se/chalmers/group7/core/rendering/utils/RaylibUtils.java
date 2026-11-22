package se.chalmers.group7.core.rendering.utils;

import static com.raylib.Raylib.*;

import com.raylib.Raylib.*;

public class RaylibUtils {
  public static Vector2 rotatePoint(Vector2 point, Vector2 pivot, float angleDeg) {
    float rad = (float) Math.toRadians(angleDeg);
    float s = (float) Math.sin(rad);
    float c = (float) Math.cos(rad);

    float dx = point.x() - pivot.x();
    float dy = point.y() - pivot.y();

    return new Vector2().x(pivot.x() + dx * c - dy * s).y(pivot.y() + dx * s + dy * c);
  }

  public static void DrawRectangleLinesPro(
      Rectangle dest, Vector2 origin, float rotation, float thick, Color color) {
    // Pivot in world space
    Vector2 pivot = new Vector2().x(dest.x()).y(dest.y());

    // Unrotated corners relative to pivot
    Vector2 tl = new Vector2().x(pivot.x() - origin.x()).y(pivot.y() - origin.y());

    Vector2 tr = new Vector2().x(tl.x() + dest.width()).y(tl.y());

    Vector2 br = new Vector2().x(tl.x() + dest.width()).y(tl.y() + dest.height());

    Vector2 bl = new Vector2().x(tl.x()).y(tl.y() + dest.height());

    // Rotate corners around pivot
    tl = rotatePoint(tl, pivot, rotation);
    tr = rotatePoint(tr, pivot, rotation);
    br = rotatePoint(br, pivot, rotation);
    bl = rotatePoint(bl, pivot, rotation);

    // Draw the four outline edges
    DrawLineEx(tl, tr, thick, color);
    DrawLineEx(tr, br, thick, color);
    DrawLineEx(br, bl, thick, color);
    DrawLineEx(bl, tl, thick, color);
  }
}
