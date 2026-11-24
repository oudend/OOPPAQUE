package se.chalmers.group7.core.tools;

import static com.raylib.Raylib.GetFrameTime;

import com.raylib.Raylib;
import java.util.Optional;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.operations.CanvasDrawOperation;
import se.chalmers.group7.core.operations.Operation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.LayerPreview;
import se.chalmers.group7.core.ui.controller.Input;

public class Eraser implements Tool {

  private final OperationManager operationManager;
  private final LayerManager layerManager;
  private final Camera camera;

  private final NumericSetting size;

  private boolean pointerDown = false;

  private final DrawingToolHelper helper = new DrawingToolHelper();

  private Canvas strokeCanvas;

  public Eraser(
          LayerManager layerManager, OperationManager operationManager, Camera camera, NumericSetting size) {
    this.layerManager = layerManager;
    this.camera = camera;
      this.size = size;
    this.operationManager = operationManager;

    helper.setBlendingMode(BlendingMode.REPLACE);
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
    if (pointerDown) onPointerUp();

    pointerDown = true;

    Layer activeLayer = layerManager.getActiveLayer();

    strokeCanvas = new RaylibCanvas(activeLayer.getWidth(), activeLayer.getHeight());

    helper.setCanvas(strokeCanvas);
    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);
    helper.startDrawing((int) layerCoords.x(), (int) layerCoords.y());
    helper.update((int) layerCoords.x(), (int) layerCoords.y(), size.getValue());
  }

  public void onPointerMove(int x, int y) {
    if (!pointerDown) return;

    Layer activeLayer = layerManager.getActiveLayer();

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

    helper.update((int) layerCoords.x(), (int) layerCoords.y(), size.getValue());
  }

  public void onPointerUp() {
    if (!pointerDown) return;

    Operation operation =
        new CanvasDrawOperation(
            layerManager.getActiveCanvas(),
            BlendingMode.LINEAR_SUBTRACTIVE_ERASE,
            strokeCanvas.getTexture());

    operationManager.addOperation(operation);

    pointerDown = false;

    strokeCanvas.clear();
  }

  @Override
  public Optional<LayerPreview> getLayerPreview() {
    if (strokeCanvas == null) return Optional.empty();

    return Optional.of(
        new LayerPreview(strokeCanvas.getTexture(), BlendingMode.LINEAR_SUBTRACTIVE_ERASE));
  }

  @Override
  public void onSelected() {}

  @Override
  public void deactivate() {
    onPointerUp();
  }
}
