package se.chalmers.group7.core.layers;

import com.raylib.Raylib;
import se.chalmers.group7.core.rendering.Camera;

public class LayerUtils {
  public enum HandleType {
    ROTATION,
    SCALE_CORNER,
    SCALE_WIDTH,
    SCALE_HEIGHT
  }

  public record Handle(HandleType type, Raylib.Vector2 worldCoords) {}

  public static Handle[] getLayerWorldHandles(Layer layer, Camera camera) {
    float width = layer.getWidth();
    float height = layer.getHeight();

    float halfWidth = width * 0.5f;
    float halfHeight = height * 0.5f;

    Handle[] handles = {
      // Corners
      new Handle(HandleType.SCALE_CORNER, camera.layerCoordinatesToWorld(layer, 0, 0)),
      new Handle(HandleType.SCALE_CORNER, camera.layerCoordinatesToWorld(layer, width, 0)),
      new Handle(HandleType.SCALE_CORNER, camera.layerCoordinatesToWorld(layer, width, height)),
      new Handle(HandleType.SCALE_CORNER, camera.layerCoordinatesToWorld(layer, 0, height)),

      // Edge midpoints
      new Handle(HandleType.SCALE_WIDTH, camera.layerCoordinatesToWorld(layer, 0, halfHeight)),
      new Handle(HandleType.SCALE_WIDTH, camera.layerCoordinatesToWorld(layer, width, halfHeight)),
      new Handle(HandleType.SCALE_HEIGHT, camera.layerCoordinatesToWorld(layer, halfWidth, 0)),
      new Handle(HandleType.SCALE_HEIGHT, camera.layerCoordinatesToWorld(layer, halfWidth, height)),

      // rotation
      new Handle(HandleType.ROTATION, camera.layerCoordinatesToWorld(layer, halfWidth, -80)),
    };

    return handles;
  }

  public static Handle getNearestHandle(Layer layer, Camera camera, float worldX, float worldY) {
    Handle[] handles = getLayerWorldHandles(layer, camera);

    Raylib.Vector2 worldVector = new Raylib.Vector2().x(worldX).y(worldY);

    Float minDistance = null;
    int closestIndex = -1;

    for (int i = 0; i < handles.length; i++) {
      Handle handle = handles[i];

      float handleDistance = Raylib.Vector2Distance(worldVector, handle.worldCoords());

      if (minDistance == null || handleDistance < minDistance) {
        minDistance = handleDistance;
        closestIndex = i;
      }
    }

    return handles[closestIndex];
  }
}
