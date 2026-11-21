package se.chalmers.group7.core.tools;

import static com.raylib.Colors.BLACK;
import static com.raylib.Raylib.GetFrameTime;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerPreviewManager;
import se.chalmers.group7.core.operations.CanvasDrawOperation;
import se.chalmers.group7.core.operations.Operation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.ui.controller.Input;

public class Brush implements Tool {

  private final OperationManager operationManager;
  private final LayerManager layerManager;
  private LayerPreviewManager previewManager;
  private final Camera camera;
  private int radius;

  private Integer previousX;
  private Integer previousY;

  private boolean pointerDown = false;

  public Brush(
      LayerManager layerManager,
      LayerPreviewManager previewManager,
      OperationManager operationManager,
      Camera camera,
      int radius) {
    this.layerManager = layerManager;
    this.previewManager = previewManager;
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

  public void onPointerDown(int x, int y) {
    pointerDown = true;

    Layer activeLayer = layerManager.getActiveLayer();

    previewManager.setPreviewCanvas(
        new RaylibCanvas(activeLayer.getWidth(), activeLayer.getHeight()));
    previewManager.setPreviewBlending(BlendingMode.NORMAL);

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

    previewManager
        .getPreviewCanvas()
        .drawCircle(
            (int) layerCoords.x(), (int) layerCoords.y(), radius, BLACK, BlendingMode.NORMAL);

    previousX = (int) layerCoords.x();
    previousY = (int) layerCoords.y();
  }

  public void onPointerMove(int x, int y) {
    if (!pointerDown) return;

    Layer activeLayer = layerManager.getActiveLayer();

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

    previewManager
        .getPreviewCanvas()
        .drawCircle(
            (int) layerCoords.x(), (int) layerCoords.y(), radius, BLACK, BlendingMode.NORMAL);
    previewManager
        .getPreviewCanvas()
        .drawLine(
            previousX,
            previousY,
            (int) layerCoords.x(),
            (int) layerCoords.y(),
            radius * 2.0f,
            BLACK,
            BlendingMode.NORMAL);
    previewManager
        .getPreviewCanvas()
        .drawCircle(previousX, previousY, radius, BLACK, BlendingMode.NORMAL);

    previousX = (int) layerCoords.x();
    previousY = (int) layerCoords.y();
  }

  public void onPointerUp() {
    Operation operation =
        new CanvasDrawOperation(
            layerManager.getActiveCanvas(), BlendingMode.NORMAL, previewManager.getTexture());

    operationManager.addOperation(operation);

    pointerDown = false;

    previousX = null;
    previousY = null;

    previewManager.clear();
  }

  @Override
  public void onSelected() {}

  @Override
  public void deactivate() {
    onPointerUp();
  }
}
