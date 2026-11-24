package se.chalmers.group7.core.tools;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;

public class LineTool extends StrokeTool {

  public LineTool(OperationManager operationManager, LayerManager layerManager, Camera camera) {
    super(operationManager, layerManager, camera);
  }

  @Override
  public void onPointerMove(int x, int y) {
    // empty code stump

    if (!pointerDown) return;

    drawStroke(x, y, false);
  }

  @Override
  protected BlendingMode getBlendingMode() {
    return BlendingMode.NORMAL;
  }
}
