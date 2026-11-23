package se.chalmers.group7.core.tools;

import static com.raylib.Colors.BLACK;
import static com.raylib.Raylib.ColorAlpha;

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

public class Brush implements Tool, Resizable, OpacityAdjustable {

  private final OperationManager operationManager;
  private final LayerManager layerManager;
  private LayerPreviewManager previewManager;
  private final Camera camera;

  private final NumericSetting size;
  private final NumericSetting opacity;

  private Integer previousX;
  private Integer previousY;

  private boolean pointerDown = false;

  public Brush(
      LayerManager layerManager,
      LayerPreviewManager previewManager,
      OperationManager operationManager,
      Camera camera,
      NumericSetting size,
      NumericSetting opacity) {
    this.layerManager = layerManager;
    this.previewManager = previewManager;
    this.operationManager = operationManager;
    this.camera = camera;
    this.size = size;
    this.opacity = opacity;
  }

  private Raylib.Color getColor() {
    return ColorAlpha(BLACK, opacity.getValue());
  }

  @Override
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
            (int) layerCoords.x(),
            (int) layerCoords.y(),
            size.getValue(),
            getColor(),
            BlendingMode.REPLACE);

    previousX = (int) layerCoords.x();
    previousY = (int) layerCoords.y();
  }

  @Override
  public void onPointerMove(int x, int y) {
    if (!pointerDown) return;

    Layer activeLayer = layerManager.getActiveLayer();

    Raylib.Vector2 layerCoords = camera.screenToLayerCoordinates(activeLayer, x, y);

    previewManager
        .getPreviewCanvas()
        .drawCircle(
            (int) layerCoords.x(),
            (int) layerCoords.y(),
            size.getValue(),
            getColor(),
            BlendingMode.REPLACE);
    previewManager
        .getPreviewCanvas()
        .drawLine(
            previousX,
            previousY,
            (int) layerCoords.x(),
            (int) layerCoords.y(),
            size.getValue() * 2.0f,
            getColor(),
            BlendingMode.REPLACE);
    previewManager
        .getPreviewCanvas()
        .drawCircle(previousX, previousY, size.getValue(), getColor(), BlendingMode.REPLACE);

    previousX = (int) layerCoords.x();
    previousY = (int) layerCoords.y();
  }

  @Override
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
  public void deactivate() {
    // not really handle-able right now
  }

  @Override
  public NumericSetting getSize() {
    return this.size;
  }

  @Override
  public NumericSetting getOpacity() {
    return this.opacity;
  }
}
