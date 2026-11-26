package se.chalmers.group7.core.tools.stroke;

import com.raylib.Colors;
import java.util.List;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.tools.Tool;
import se.chalmers.group7.core.tools.settings.BoundedFloatSetting;
import se.chalmers.group7.core.tools.settings.ToolSettings;

public class Eraser extends GenericBrush implements Tool {

  ToolSettings settings =
      new ToolSettings(
          List.of(
              new BoundedFloatSetting(CommonStrokeSettings.SIZE, 20f, 1f, 100f),
              new BoundedFloatSetting(CommonStrokeSettings.OPACITY, 1f, 0f, 1f)));

  public Eraser(OperationManager operationManager, LayerManager layerManager, Camera camera) {
    super(operationManager, layerManager, camera);
  }

  @Override
  protected StrokeParams getStrokeParams() {
    return new StrokeParams(
        settings.getSettingValue(CommonStrokeSettings.SIZE),
        settings.getSettingValue(CommonStrokeSettings.OPACITY),
        Colors.WHITE,
        BlendingMode.REPLACE,
        BlendingMode.LINEAR_SUBTRACTIVE_ERASE);
  }

  @Override
  public ToolSettings getSettings() {
    return settings;
  }
}
