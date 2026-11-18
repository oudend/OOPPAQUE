package se.chalmers.group7.core.rendering;

import static com.raylib.Colors.*;

import com.raylib.Raylib;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.rendering.utils.RaylibUtils;

public class LayerGuideRenderer {
  public static void render(Camera camera, Layer layer) {
    float zoom = camera.getZoom();

    Raylib.Vector2 screenPosition = camera.worldToScreen(layer.getX(), layer.getY());

    // Layer's dimensions after zooming
    float width = layer.getWidth() * zoom;
    float height = layer.getHeight() * zoom;

    // Rotation pivot: center of the layer, in screen pixels
    Raylib.Vector2 origin = new Raylib.Vector2().x(width / 2.0f).y(height / 2.0f);

    // Destination position is the pivot's screen position
    Raylib.Rectangle destination =
        new Raylib.Rectangle()
            .x(screenPosition.x() + origin.x())
            .y(screenPosition.y() + origin.y())
            .width(width)
            .height(height);

    RaylibUtils.DrawRectangleLinesPro(destination, origin, layer.getRotation(), 2f, BLUE);

    float halfWidth = width / 2.0f;
    float halfHeight = height / 2.0f;

    Raylib.Vector2[] points = {
      // Corners
      new Raylib.Vector2().x(-halfWidth).y(-halfHeight),
      new Raylib.Vector2().x(halfWidth).y(-halfHeight),
      new Raylib.Vector2().x(halfWidth).y(halfHeight),
      new Raylib.Vector2().x(-halfWidth).y(halfHeight),

      // Edge midpoints
      new Raylib.Vector2().x(0).y(-halfHeight),
      new Raylib.Vector2().x(halfWidth).y(0),
      new Raylib.Vector2().x(0).y(halfHeight),
      new Raylib.Vector2().x(-halfWidth).y(0)
    };

    for (Raylib.Vector2 point : points) {
      // Rotate around the rectangle's center
      Raylib.Vector2 rotated =
          Raylib.Vector2Rotate(point, (float) Math.toRadians(layer.getRotation()));

      // Convert to screen coordinates
      float x = destination.x() + rotated.x();
      float y = destination.y() + rotated.y();

      Raylib.DrawCircleV(new Raylib.Vector2().x(x).y(y), 6f, RED);
    }
  }
}
