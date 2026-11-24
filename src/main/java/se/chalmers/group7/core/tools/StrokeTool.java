package se.chalmers.group7.core.tools;

import static com.raylib.Colors.BLACK;
import static com.raylib.Raylib.ColorAlpha;
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

abstract class StrokeTool implements Tool, Resizable, OpacityAdjustable {
  private final OperationManager operationManager;
  private final LayerManager layerManager;
  private final Camera camera;

  private final NumericSetting size;
  private final NumericSetting opacity;

  private boolean pointerDown = false;
  private Canvas strokeCanvas;

  int previousX = 0;
  int previousY = 0;

  StrokeTool(OperationManager operationManager, LayerManager layerManager, Camera camera) {
    this.operationManager = operationManager;
    this.layerManager = layerManager;
    this.camera = camera;
    this.size = new NumericSetting(NumericSettingType.SIZE);
    this.opacity = new NumericSetting(NumericSettingType.OPACITY);
  }

  protected abstract BlendingMode getBlendingMode();

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
            layerManager.getActiveCanvas(), getBlendingMode(), strokeCanvas.getTexture());

    operationManager.addOperation(operation);

    pointerDown = false;

    strokeCanvas.clear();
  }

  @Override
  public Optional<LayerPreview> getLayerPreview() {
    if (strokeCanvas == null) return Optional.empty();

    return Optional.of(new LayerPreview(strokeCanvas.getTexture(), getBlendingMode()));
  }

  @Override
  public void onSelected() {}

  @Override
  public void deactivate() {
    onPointerUp();
  }

  @Override
  public NumericSetting getSize() {
    return this.size;
  }

  @Override
  public NumericSetting getOpacity() {
    return this.opacity;
  }

  public Raylib.Color getColor() {
    return ColorAlpha(BLACK, opacity.getValue());
  }

  private void drawStroke(int x, int y) {
    float radius = size.getValue();

    strokeCanvas.drawCircle(previousX, previousY, radius, getColor(), BlendingMode.REPLACE);
    strokeCanvas.drawLine(
        previousX, previousY, x, y, radius * 2.0f, getColor(), BlendingMode.REPLACE);
    strokeCanvas.drawCircle(x, y, radius, getColor(), BlendingMode.REPLACE);

    previousX = x;
    previousY = y;
  }
}
