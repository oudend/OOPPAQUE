package se.chalmers.group7.core.tools;

import static com.raylib.Colors.BLACK;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;

public class DrawingToolHelper {
  private Canvas canvas;
  private BlendingMode blendingMode;

  private float radius = 10f;

  int previousX = 0;
  int previousY = 0;

  public DrawingToolHelper(Canvas canvas, BlendingMode blendingMode, float radius) {
    this.canvas = canvas;
    this.blendingMode = blendingMode;
    this.radius = radius;
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

  public void update(int x, int y) {
    canvas.drawCircle(previousX, previousY, radius, BLACK, BlendingMode.NORMAL);
    canvas.drawLine(previousX, previousY, x, y, radius * 2.0f, BLACK, BlendingMode.NORMAL);
    canvas.drawCircle(x, y, radius, BLACK, BlendingMode.NORMAL);

    previousX = x;
    previousY = y;
  }
}
