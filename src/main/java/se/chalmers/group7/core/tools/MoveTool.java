package se.chalmers.group7.core.tools;

import com.raylib.Raylib;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.operations.LayerTransformOperation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;

public class MoveTool implements Tool {
  private OperationManager operationManager;

  private enum TransformType {
    MOVE,
    RESIZE,
    ROTATE
  }

  private final LayerManager layerManager;
  private final Camera camera;

  private int activeLayerIndex = -1;
  private TransformType transformType;

  private LayerTransform initialTransform;

  private Integer previousX;
  private Integer previousY;

  public MoveTool(LayerManager layerManager, OperationManager operationManager, Camera camera) {
    this.layerManager = layerManager;
    this.camera = camera;
    this.operationManager = operationManager;
  }

  private int layerIntersection(int x, int y) {
    // input: global coords.

    Raylib.Vector2 worldVector = camera.screenToWorld(x, y);

    for (int i = 0; i < layerManager.getLayerCount(); i++) {
      Layer layer = layerManager.getLayer(i);

      boolean intersects =
          (worldVector.x() >= layer.getX() && worldVector.x() <= layer.getX() + layer.getWidth())
              && (worldVector.y() >= layer.getY()
                  && worldVector.y() <= layer.getY() + layer.getHeight());

      if (intersects) {
        return i;
      }
    }

    return -1;
  }

  @Override
  public void onPointerDown(int x, int y) {
    activeLayerIndex = layerIntersection(x, y);

    if (activeLayerIndex == -1) return;

    layerManager.setActiveLayer(activeLayerIndex);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    transformType = TransformType.MOVE;
    initialTransform = activeLayer.getLayerTransform();

    previousX = x;
    previousY = y;
  }

  @Override
  public void onPointerMove(int x, int y) {
    if (activeLayerIndex == -1) return;

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    float deltaX = ((x - previousX) / camera.getZoom());
    float deltaY = ((y - previousY) / camera.getZoom());

    switch (transformType) {
      case MOVE:
        activeLayer.setX((int) (activeLayer.getX() + deltaX));
        activeLayer.setY((int) (activeLayer.getY() + deltaY));
        break;
    }

    previousX = x;
    previousY = y;
  }

  @Override
  public void onPointerUp() {
    if (activeLayerIndex == -1) return;

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    LayerTransform finalTransform = LayerTransform.from(activeLayer);

    if (!initialTransform.equals(finalTransform)) {
      LayerTransformOperation operation =
          new LayerTransformOperation(activeLayer, initialTransform, finalTransform, true);

      operationManager.addOperation(operation);
    }

    reset();
  }

  @Override
  public void deactivate() {

    reset();
  }

  private void reset() {
    transformType = null;
    activeLayerIndex = -1;
  }
}
