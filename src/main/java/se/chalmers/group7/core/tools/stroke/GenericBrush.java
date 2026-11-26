package se.chalmers.group7.core.tools.stroke;

import static com.raylib.Raylib.ColorAlpha;
import static com.raylib.Raylib.GetFrameTime;

import com.raylib.Raylib;
import java.util.Optional;
import se.chalmers.group7.core.canvas.Canvas;
import se.chalmers.group7.core.canvas.RaylibCanvas;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.operations.CanvasDrawOperation;
import se.chalmers.group7.core.operations.Operation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.LayerPreview;
import se.chalmers.group7.core.tools.Tool;
import se.chalmers.group7.core.ui.controller.Input;

abstract class GenericBrush implements Tool {
  private final OperationManager operationManager;
  private final LayerManager layerManager;
  private final Camera camera;

  private boolean pointerDown = false;
  private Canvas strokeCanvas;

  int previousX = 0;
  int previousY = 0;

  protected abstract StrokeParams getStrokeParams();

  GenericBrush(OperationManager operationManager, LayerManager layerManager, Camera camera) {
    this.operationManager = operationManager;
    this.layerManager = layerManager;
    this.camera = camera;
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

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);
    previousX = (int) layerCoords.x();
    previousY = (int) layerCoords.y();
    drawStroke(previousX, previousY);
  }

  public void onPointerMove(int x, int y) {
    if (!pointerDown) return;

    Layer activeLayer = layerManager.getActiveLayer();

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

    drawStroke((int) layerCoords.x(), (int) layerCoords.y());
  }

  public void onPointerUp() {
    if (!pointerDown) return;

    Operation operation =
        new CanvasDrawOperation(
            layerManager.getActiveCanvas(),
            getStrokeParams().drawBlendMode(),
            strokeCanvas.getTexture());

    operationManager.addOperation(operation);

    pointerDown = false;

    strokeCanvas.clear();
  }

  @Override
  public Optional<LayerPreview> getLayerPreview() {
    if (strokeCanvas == null) return Optional.empty();

    return Optional.of(
        new LayerPreview(strokeCanvas.getTexture(), getStrokeParams().drawBlendMode()));
  }

  @Override
  public void onSelected() {}

  @Override
  public void deactivate() {
    onPointerUp();
  }

  public Raylib.Color getRaylibColor() {
    return ColorAlpha(getStrokeParams().color(), getStrokeParams().opacity());
  }

  private void drawStroke(int x, int y) {
    float radius = getStrokeParams().size();

    strokeCanvas.drawCircle(
        previousX, previousY, radius, getRaylibColor(), getStrokeParams().previewBlendMode());
    strokeCanvas.drawLine(
        previousX,
        previousY,
        x,
        y,
        radius * 2.0f,
        getRaylibColor(),
        getStrokeParams().previewBlendMode());
    strokeCanvas.drawCircle(x, y, radius, getRaylibColor(), getStrokeParams().previewBlendMode());

    previousX = x;
    previousY = y;
  }
}
