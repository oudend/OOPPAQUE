package se.chalmers.group7.core.elements.CanvasBlock;

import com.raylib.Raylib.Color;
import se.chalmers.group7.core.tools.settings.ToolSettings;
import se.chalmers.group7.core.tools.stroke.CommonStrokeSettings;
import se.chalmers.group7.core.ui.model.MouseAction;

public class AChangeColor extends MouseAction {
  private final CanvasBlock canvas;
  private final Color color;

  public AChangeColor(CanvasBlock canvas, Color color) {
    this.canvas = canvas;
    this.color = color;
  }

  @Override
  public void onMouseLeftDown() {
    // Avoid crashing when "changing color" of eraser

    ToolSettings toolSettings = canvas.getToolManager().getActiveTool().getSettings();

    if (toolSettings == null) return;

    toolSettings.updateSettingIfExists(CommonStrokeSettings.COLOR, color -> this.color);

    //    if (canvas.getToolManager().getActiveTool() instanceof ColorAdjustable adjustable) {
    //      adjustable.SetColor(this.color);
    //    }
  }
}
