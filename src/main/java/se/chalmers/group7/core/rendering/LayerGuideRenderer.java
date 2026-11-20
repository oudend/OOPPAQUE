package se.chalmers.group7.core.rendering;

import static com.raylib.Colors.*;

import com.raylib.Raylib;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerUtils;
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

    Raylib.Vector2 rotationHandleLineStart =
        camera.layerCoordinatesToWorld(layer, layer.getWidth() / 2.0f, 0);
    Raylib.Vector2 rotationHandleLineEnd =
        camera.layerCoordinatesToWorld(layer, layer.getWidth() / 2.0f, -80);

    Raylib.Vector2 rotationHandleLineStartS =
        camera.worldToScreen(rotationHandleLineStart.x(), rotationHandleLineStart.y());
    Raylib.Vector2 rotationHandleLineEndS =
        camera.worldToScreen(rotationHandleLineEnd.x(), rotationHandleLineEnd.y());

    Raylib.DrawLineEx(rotationHandleLineStartS, rotationHandleLineEndS, 2.0f, BLUE);

    for (LayerUtils.Handle handle : LayerUtils.getLayerWorldHandles(layer, camera)) {
      Raylib.Vector2 screenPoint =
          camera.worldToScreen(handle.worldCoords().x(), handle.worldCoords().y());

      Raylib.DrawCircleV(screenPoint, 6f, BLUE);
      Raylib.DrawCircleV(screenPoint, 4f, WHITE);
    }
  }
}
