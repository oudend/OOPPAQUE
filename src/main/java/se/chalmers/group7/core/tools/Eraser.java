package se.chalmers.group7.core.tools;

import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.ColorAlpha;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;
import se.chalmers.group7.core.operations.CanvasDrawOperation;
import se.chalmers.group7.core.operations.Operation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.temp.PreviewManager;

public class Eraser implements Tool {

  private final OperationManager operationManager;
  private final Canvas canvas;
  private PreviewManager previewManager;
  private Canvas previewCanvas;
  private int radius;

  private Integer previousX;
  private Integer previousY;

  private boolean pointerDown = false;

  private float opacity = 0.5f;

  public Eraser(
      Canvas canvas, PreviewManager previewManager, OperationManager operationManager, int radius) {
    this.canvas = canvas;
    this.previewManager = previewManager;
    this.previewCanvas = previewManager.getPreviewLayer();
    this.radius = radius;
    this.operationManager = operationManager;
  }

  public Raylib.Color getColor() {
    return ColorAlpha(WHITE, opacity);
  }

  @Override
  public void onPointerDown(int x, int y) {
    pointerDown = true;

    previewManager.setPreviewBlending(BlendingMode.LINEAR_SUBTRACTIVE_ERASE);
    previewCanvas.drawCircle(x, y, radius, getColor(), BlendingMode.REPLACE);

    previousX = x;
    previousY = y;
  }

  @Override
  public void onPointerMove(int x, int y) {
    if (!pointerDown) return;

    previewCanvas.drawCircle(x, y, radius, getColor(), BlendingMode.REPLACE);
    previewCanvas.drawLine(
        previousX, previousY, x, y, radius * 2.0f, getColor(), BlendingMode.REPLACE);
    previewCanvas.drawCircle(previousX, previousY, radius, getColor(), BlendingMode.REPLACE);

    previousX = x;
    previousY = y;
  }

  @Override
  public void onPointerUp() {
    Operation operation =
        new CanvasDrawOperation(
            canvas, BlendingMode.LINEAR_SUBTRACTIVE_ERASE, previewCanvas.getTexture());

    operationManager.addOperation(operation);

    pointerDown = false;

    previousX = null;
    previousY = null;

    previewManager.setPreviewBlending(BlendingMode.NORMAL);
    previewCanvas.clear();
  }

  @Override
  public void deactivate() {
    // not really handle-able right now
  }
}
