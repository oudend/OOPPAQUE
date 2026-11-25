package se.chalmers.group7.core.tools;

import com.raylib.Raylib.Color;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;

public class Brush extends StrokeTool implements ColorAdjustable {

  public Brush(
      OperationManager operationManager,
      LayerManager layerManager,
      Camera camera,
      Color drawColor) {
    super(operationManager, layerManager, camera, drawColor);
  }

  public void SetColor(Color newColor) {
    this.color.setColor(newColor);
  }

  public ColorSetting GetColor() {
    return this.color;
  }

  @Override
  protected BlendingMode getBlendingMode() {
    return BlendingMode.NORMAL;
  }
}
