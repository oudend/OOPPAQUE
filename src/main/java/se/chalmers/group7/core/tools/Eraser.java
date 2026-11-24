package se.chalmers.group7.core.tools;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;

public class Eraser extends StrokeTool {

  public Eraser(OperationManager operationManager, LayerManager layerManager, Camera camera) {
    super(operationManager, layerManager, camera);
  }

  @Override
  protected BlendingMode getBlendingMode() {
    return BlendingMode.LINEAR_SUBTRACTIVE_ERASE;
  }
}
