package se.chalmers.group7.core.tools;

import static com.raylib.Colors.BLACK;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;

public class DrawingToolHelper {
  private Canvas canvas;
  private BlendingMode blendingMode;

  int previousX = 0;
  int previousY = 0;

  public DrawingToolHelper(Canvas canvas, BlendingMode blendingMode, float radius) {
    this.canvas = canvas;
    this.blendingMode = blendingMode;
  }

  public DrawingToolHelper(Canvas canvas) {
    this(canvas, BlendingMode.NORMAL, 10f);
  }

  public DrawingToolHelper() {
    this(null, BlendingMode.NORMAL, 10f);
  }

  public void setCanvas(Canvas canvas) {
    this.canvas = canvas;
  }

  public void setBlendingMode(BlendingMode blendingMode) {
    this.blendingMode = blendingMode;
  }

  public void startDrawing(int x, int y) {
    previousX = x;
    previousY = y;
  }

  public void update(int x, int y, float size) {
    canvas.drawCircle(previousX, previousY, size, BLACK, BlendingMode.REPLACE);
    canvas.drawLine(previousX, previousY, x, y, size * 2.0f, BLACK, BlendingMode.REPLACE);
    canvas.drawCircle(x, y, size, BLACK, BlendingMode.NORMAL);

    previousX = x;
    previousY = y;
  }
}
