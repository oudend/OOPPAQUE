package se.chalmers.group7.core.tools;

import static com.raylib.Colors.BLACK;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;
import se.chalmers.group7.core.operations.CanvasDrawOperation;
import se.chalmers.group7.core.operations.Operation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.temp.PreviewManager;

public class Brush implements Tool {
  private static Tool instance;

  private final OperationManager operationManager;
  private final Canvas canvas;
  private PreviewManager previewManager;
  private Canvas previewCanvas;
  private int radius;

  private Integer previousX;
  private Integer previousY;

  private boolean pointerDown = false;

  public Brush(
      Canvas canvas, PreviewManager previewManager, OperationManager operationManager, int radius) {
    this.canvas = canvas;
    this.previewManager = previewManager;
    this.previewCanvas = previewManager.getPreviewLayer();
    this.radius = radius;
    this.operationManager = operationManager;
    instance = this;
  }

  @Override
  public void onPointerDown(int x, int y) {
    pointerDown = true;

    previewManager.setPreviewBlending(BlendingMode.NORMAL);
    previewCanvas.drawCircle(x, y, radius, BLACK, BlendingMode.NORMAL);

    previousX = x;
    previousY = y;
  }

  @Override
  public void onPointerMove(int x, int y) {
    if (!pointerDown) return;

    previewCanvas.drawCircle(x, y, radius, BLACK, BlendingMode.NORMAL);
    previewCanvas.drawLine(previousX, previousY, x, y, radius * 2.0f, BLACK, BlendingMode.NORMAL);
    previewCanvas.drawCircle(previousX, previousY, radius, BLACK, BlendingMode.NORMAL);

    previousX = x;
    previousY = y;
  }

  @Override
  public void onPointerUp() {
    Operation operation =
        new CanvasDrawOperation(canvas, BlendingMode.NORMAL, previewCanvas.getTexture());

    operationManager.addOperation(operation);

    pointerDown = false;

    previousX = null;
    previousY = null;

    previewCanvas.clear();
  }

  @Override
  public void deactivate() {
    // not really handle-able right now
  }

  public static Tool getInstance() {
    return instance;
  }
}
