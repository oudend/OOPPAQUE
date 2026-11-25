package se.chalmers.group7.core.elements.CanvasBlock;

import com.raylib.Raylib.Color;
import se.chalmers.group7.core.tools.Brush;
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
    if (canvas.getToolManager().getActiveTool() instanceof Brush) {
      // TODO: implement
      // ((Brush) canvas.getToolManager().getActiveTool()).setColor(this.color);
      System.out.println("Tyring to change color but not implemented yet");
    }
  }
}
