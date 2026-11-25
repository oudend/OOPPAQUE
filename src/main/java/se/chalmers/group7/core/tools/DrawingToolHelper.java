package se.chalmers.group7.core.tools;

import static com.raylib.Colors.BLACK;

import com.raylib.Raylib.Color;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;

public class DrawingToolHelper {
  private Canvas canvas;
  private BlendingMode blendingMode;
  // default color for drawing things is black (including eraser)
  private Color drawColor = BLACK;

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

  public void update(int x, int y, Color col) {
    drawColor = col;
    this.update(x, y);
  }

  public void update(int x, int y) {
    canvas.drawCircle(previousX, previousY, radius, drawColor, blendingMode);
    canvas.drawLine(previousX, previousY, x, y, radius * 2.0f, drawColor, blendingMode);
    canvas.drawCircle(x, y, radius, drawColor, blendingMode);

    previousX = x;
    previousY = y;
  }
}
