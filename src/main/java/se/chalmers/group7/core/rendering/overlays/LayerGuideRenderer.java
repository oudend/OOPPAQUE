package se.chalmers.group7.core.rendering.overlays;

import static com.raylib.Colors.*;

import com.raylib.Raylib;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.utils.RaylibUtils;

public class LayerGuideRenderer implements ScreenOverlay {
  private LayerManager layerManager;

  public LayerGuideRenderer(LayerManager layerManager) {
    this.layerManager = layerManager;
  }

  public void render(Camera camera) {
    if (layerManager.getLayerCount() == 0) return;

    for (int i = 0; i < layerManager.getLayerCount(); i++) {
      Layer layer = layerManager.getLayer(i);

      renderLayer(camera, layer, i == layerManager.getActiveLayerIndex());
    }
  }

  private void renderLayer(Camera camera, Layer layer, boolean renderHandles) {
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

    LayerUtils.BoundingBox mainBox = LayerUtils.getBoundingBox(layer);

    Raylib.Vector2 screenBoxPosition = camera.worldToScreen(mainBox.x(), mainBox.y());

    Raylib.Rectangle boxDestination =
        new Raylib.Rectangle()
            .x(screenBoxPosition.x() + origin.x())
            .y(screenBoxPosition.y() + origin.y())
            .width(mainBox.width() * zoom)
            .height(mainBox.height() * zoom);

    RaylibUtils.DrawRectangleLinesPro(boxDestination, origin, 0f, 1f, BLUE);

    if (renderHandles) renderLayerHandles(camera, layer);
  }

  private void renderLayerHandles(Camera camera, Layer layer) {
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
