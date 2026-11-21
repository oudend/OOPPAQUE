package se.chalmers.group7.core.tools;

import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.ColorAlpha;
import static com.raylib.Raylib.GetFrameTime;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerPreviewManager;
import se.chalmers.group7.core.operations.CanvasDrawOperation;
import se.chalmers.group7.core.operations.Operation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.ui.controller.Input;

public class Eraser implements Tool {

  private final OperationManager operationManager;
  private final LayerManager layerManager;
  private LayerPreviewManager previewManager;
  private Canvas previewCanvas;
  private final Camera camera;
  private int radius;

  private Integer previousX;
  private Integer previousY;

  private boolean pointerDown = false;

  private float opacity = 0.5f;

  public Eraser(
      LayerManager layerManager,
      LayerPreviewManager previewManager,
      OperationManager operationManager,
      Camera camera,
      int radius) {
    this.layerManager = layerManager;
    this.previewManager = previewManager;
    this.previewCanvas = previewManager.getPreviewCanvas();
    this.camera = camera;
    this.radius = radius;
    this.operationManager = operationManager;
  }

  @Override
  public void onInput(Input input) {
    if (input.mouseLeft().down()) {
      onPointerDown(input.xPos(), input.yPos());
    }

    if (input.mouseLeft().released()) {
      onPointerUp();
    }

    if ((input.deltaX() * input.deltaX() + input.deltaY() * input.deltaY()) > GetFrameTime()) {
      onPointerMove(input.xPos(), input.yPos());
    }
  }

  public Raylib.Color getColor() {
    return ColorAlpha(WHITE, opacity);
  }

  public void onPointerDown(int x, int y) {
    pointerDown = true;

    Layer activeLayer = layerManager.getActiveLayer();

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

    previewManager.setPreviewCanvas(
        new RaylibCanvas(activeLayer.getWidth(), activeLayer.getHeight()));
    previewManager.setPreviewBlending(BlendingMode.LINEAR_SUBTRACTIVE_ERASE);

    previewCanvas = previewManager.getPreviewCanvas();

    previewCanvas.drawCircle(
        (int) layerCoords.x(), (int) layerCoords.y(), radius, getColor(), BlendingMode.REPLACE);

    previousX = (int) layerCoords.x();
    previousY = (int) layerCoords.y();
  }

  public void onPointerMove(int x, int y) {
    if (!pointerDown) return;

    Layer activeLayer = layerManager.getActiveLayer();

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

    previewCanvas.drawCircle(
        (int) layerCoords.x(), (int) layerCoords.y(), radius, getColor(), BlendingMode.REPLACE);
    previewCanvas.drawLine(
        previousX,
        previousY,
        (int) layerCoords.x(),
        (int) layerCoords.y(),
        radius * 2.0f,
        getColor(),
        BlendingMode.REPLACE);
    previewCanvas.drawCircle(previousX, previousY, radius, getColor(), BlendingMode.REPLACE);

    previousX = (int) layerCoords.x();
    previousY = (int) layerCoords.y();
  }

  public void onPointerUp() {
    Operation operation =
        new CanvasDrawOperation(
            layerManager.getActiveCanvas(),
            BlendingMode.LINEAR_SUBTRACTIVE_ERASE,
            previewCanvas.getTexture());

    operationManager.addOperation(operation);

    pointerDown = false;

    previousX = null;
    previousY = null;

    previewManager.setPreviewBlending(BlendingMode.NORMAL);
    previewCanvas.clear();
  }

  @Override
  public void deactivate() {
    onPointerUp();
  }

  @Override
  public void onSelected() {}
}
