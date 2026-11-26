package se.chalmers.group7.core.tools.move;

import static com.raylib.Raylib.*;

import com.raylib.Raylib;
import java.util.*;
import se.chalmers.group7.core.alignment.AlignmentGuideRenderer;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.operations.LayerTransformOperation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.overlays.CompositeScreenOverlay;
import se.chalmers.group7.core.rendering.overlays.LayerGuideRenderer;
import se.chalmers.group7.core.rendering.overlays.ScreenOverlay;
import se.chalmers.group7.core.tools.Tool;
import se.chalmers.group7.core.ui.controller.Input;

public class MoveTool implements Tool {
  private enum TransformType {
    MOVE,
    RESIZE,
    ROTATE
  }

  private OperationManager operationManager;
  private final LayerManager layerManager;
  private final Camera camera;

  private int activeLayerIndex = -1;
  private TransformType transformType;
  private LayerTransform initialTransform;

  private final ResizingHelper resizingHelper = new ResizingHelper();
  private final MovingHelper movingHelper = new MovingHelper();

  private final LayerGuideRenderer guideOverlay;
  private final AlignmentGuideRenderer alignmentOverlay;
  private final CompositeScreenOverlay screenOverlay;

  private boolean snappingEnabled = true;

  public MoveTool(LayerManager layerManager, OperationManager operationManager, Camera camera) {
    this.layerManager = layerManager;
    this.camera = camera;
    this.operationManager = operationManager;
    this.guideOverlay = new LayerGuideRenderer(this.layerManager);
    this.alignmentOverlay = new AlignmentGuideRenderer();
    this.screenOverlay = new CompositeScreenOverlay(List.of(guideOverlay, alignmentOverlay));
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

    snappingEnabled = !input.keyCodes().contains(KEY_LEFT_CONTROL);
  }

  public void onPointerDown(int x, int y) {
    Raylib.Vector2 worldVector = camera.screenToWorld(x, y);
    Raylib.Vector2 screenVector = new Raylib.Vector2().x(x).y(y);

    activeLayerIndex = LayerUtils.layerIntersection(layerManager.getLayers(), camera, x, y);

    boolean intersection = activeLayerIndex != -1;

    if (!intersection) activeLayerIndex = layerManager.getActiveLayerIndex();

    layerManager.setActiveLayer(activeLayerIndex);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    movingHelper.selectLayer(camera, activeLayer, x, y);

    LayerUtils.Handle nearestHandle =
        LayerUtils.getNearestHandle(activeLayer, camera, worldVector.x(), worldVector.y());

    Raylib.Vector2 handleScreenCoords =
        camera.worldToScreen(nearestHandle.worldCoords().x(), nearestHandle.worldCoords().y());

    float handleDistance = Raylib.Vector2Distance(handleScreenCoords, screenVector);

    if (!intersection && handleDistance > 6f) {
      reset();
      return;
    }

    transformType = TransformType.MOVE;

    if (handleDistance <= 6f) {
      switch (nearestHandle.type()) {
        case ROTATION -> transformType = TransformType.ROTATE;
        case SCALE_CORNER, SCALE_WIDTH, SCALE_HEIGHT -> transformType = TransformType.RESIZE;
      }
    }

    resizingHelper.selectLayer(activeLayer, nearestHandle);

    initialTransform = activeLayer.getLayerTransform();
  }

  public void onPointerMove(int x, int y) {
    if (activeLayerIndex == -1) return;

    switch (transformType) {
      case MOVE:
        movingHelper.move(camera, layerManager.getLayers(), x, y, snappingEnabled);
        alignmentOverlay.setLines(movingHelper.getOverlayLines());
        break;
      case RESIZE:
        resizingHelper.resize(camera, layerManager.getLayers(), x, y);
        alignmentOverlay.setLines(resizingHelper.getOverlayLines());
        break;
      case ROTATE:
        RotationHelper.rotate(camera, layerManager.getActiveLayer(), x, y);
        break;
    }
  }

  public void onPointerUp() {
    if (activeLayerIndex == -1) return;

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    LayerTransform finalTransform = LayerTransform.from(activeLayer);

    alignmentOverlay.setLines(Collections.emptyList());

    if (!initialTransform.equals(finalTransform)) {
      LayerTransformOperation operation =
          new LayerTransformOperation(activeLayer, initialTransform, finalTransform, true);

      operationManager.addOperation(operation);
    }

    reset();
  }

  @Override
  public void deactivate() {
    reset();
  }

  private void reset() {
    transformType = null;
    activeLayerIndex = -1;
  }

  @Override
  public void onSelected() {}

  @Override
  public Optional<ScreenOverlay> getScreenOverlay() {
    return Optional.of(screenOverlay);
  }
}
