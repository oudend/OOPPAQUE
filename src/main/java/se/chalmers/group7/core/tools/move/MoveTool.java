package se.chalmers.group7.core.tools.move;

import static com.raylib.Raylib.*;

import com.raylib.Raylib;
import java.util.*;
import se.chalmers.group7.core.alignment.AlignmentGuideRenderer;
import se.chalmers.group7.core.alignment.AlignmentLine;
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

  private LayerUtils.Handle activeHandle;

  private Vector2 dragStartPosition;
  private Vector2 dragStartMouseWorld;

  private final LayerGuideRenderer guideOverlay;
  private final AlignmentGuideRenderer alignmentOverlay;
  private final CompositeScreenOverlay screenOverlay;

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
  }

  public void onPointerDown(int x, int y) {
    Raylib.Vector2 worldVector = camera.screenToWorld(x, y);
    Raylib.Vector2 screenVector = new Raylib.Vector2().x(x).y(y);

    activeLayerIndex = LayerUtils.layerIntersection(layerManager.getLayers(), camera, x, y);

    boolean intersection = activeLayerIndex != -1;

    if (!intersection) activeLayerIndex = layerManager.getActiveLayerIndex();

    layerManager.setActiveLayer(activeLayerIndex);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    dragStartPosition = new Vector2().x(activeLayer.getX()).y(activeLayer.getY());

    dragStartPosition = new Vector2().x(activeLayer.getX()).y(activeLayer.getY());
    dragStartMouseWorld = worldVector;

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

    if (transformType != TransformType.MOVE) activeHandle = nearestHandle;
    initialTransform = activeLayer.getLayerTransform();
  }

  private static final float SNAP_ENTER_DISTANCE = 20f;
  private static final float SNAP_EXIT_DISTANCE = 45f;
  private static final float SNAP_SWITCH_THRESHOLD = 15f;
  private static final float SNAP_PREVIEW_DISTANCE = 15f;
  private static final float SNAP_INTERSECTION_DISTANCE = 7.5f;

  private static final float SNAP_ANGLE_DEGREES = 90f;
  private static final float SNAP_ANGLE_THRESHOLD = 10f;

  private MoveToolUtils.LineMatch activeMatch;

  public void onPointerMove(int x, int y) {
    if (activeLayerIndex == -1) return;

    Raylib.Vector2 worldVector = camera.screenToWorld(x, y);

    Layer activeLayer = layerManager.getLayer(activeLayerIndex);

    switch (transformType) {
      case MOVE:
        Raylib.Vector2 mouseDelta = Vector2Subtract(worldVector, dragStartMouseWorld);
        Raylib.Vector2 mouseIdeal = Vector2Add(dragStartPosition, mouseDelta);

        List<MoveToolUtils.LineMatch> matches =
            MoveToolUtils.getAllAlignmentMatches(camera, layerManager, mouseIdeal);

        MoveToolUtils.AlignedLineMatches alignedLineMatches =
            MoveToolUtils.alignedLineMatches(camera, matches, mouseIdeal);

        MoveToolUtils.LineMatch closestMatch = alignedLineMatches.closest();

        MoveToolUtils.LineMatch oppositeClosestMatch = alignedLineMatches.other();

        Float activeScreenDistance = null;
        Vector2 activeSlidingPosition = null;

        if (activeMatch != null) {
          activeSlidingPosition = MoveToolUtils.getSlidingPosition(mouseIdeal, activeMatch);

          activeScreenDistance =
              Vector2Distance(
                  camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y()),
                  camera.worldToScreen(activeSlidingPosition.x(), activeSlidingPosition.y()));
        }

        Vector2 closestSlidingPosition = MoveToolUtils.getSlidingPosition(mouseIdeal, closestMatch);

        float closestScreenDistance =
            Vector2Distance(
                camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y()),
                camera.worldToScreen(closestSlidingPosition.x(), closestSlidingPosition.y()));

        if (activeMatch != null) {
          if (activeScreenDistance > SNAP_EXIT_DISTANCE) {
            activeMatch = null;
          }
        }
        if (activeMatch == null) {
          if (closestScreenDistance < SNAP_ENTER_DISTANCE) {
            activeMatch = closestMatch;
            activeSlidingPosition = closestSlidingPosition;
          }
        } else if (activeMatch != closestMatch
            && closestScreenDistance < activeScreenDistance - SNAP_SWITCH_THRESHOLD) {
          activeMatch = closestMatch;
          activeSlidingPosition = closestSlidingPosition;
        }

        if (activeMatch != null) {
          MoveToolUtils.LineMatch oppositeActiveMatch =
              activeMatch.type() == closestMatch.type() ? oppositeClosestMatch : closestMatch;

          Vector2 intersectionPosition =
              MoveToolUtils.getIntersectionPosition(
                  new Vector2().x(activeSlidingPosition.x()).y(activeSlidingPosition.y()),
                  activeMatch,
                  oppositeActiveMatch);

          float intersectionScreenDistance =
              Vector2Distance(
                  camera.worldToScreen(activeSlidingPosition.x(), activeSlidingPosition.y()),
                  camera.worldToScreen(intersectionPosition.x(), intersectionPosition.y()));

          if (intersectionScreenDistance < SNAP_INTERSECTION_DISTANCE)
            activeSlidingPosition = intersectionPosition;
        }

        List<MoveToolUtils.LineMatch> closeMatches =
            MoveToolUtils.findCloseMatches(
                camera,
                matches,
                mouseIdeal,
                activeMatch == null
                    ? SNAP_ENTER_DISTANCE + SNAP_PREVIEW_DISTANCE
                    : SNAP_EXIT_DISTANCE + SNAP_PREVIEW_DISTANCE);

        if (!closeMatches.isEmpty()) {
          List<AlignmentLine> comparisonLines =
              closeMatches.stream().map(MoveToolUtils.LineMatch::comparisonLine).toList();

          alignmentOverlay.setLines(comparisonLines);
        } else {
          alignmentOverlay.setLines(Collections.emptyList());
        }

        if (activeMatch != null) {
          activeLayer.setX((int) activeSlidingPosition.x());

          activeLayer.setY((int) activeSlidingPosition.y());
          break;
        }

        activeLayer.setX((int) mouseIdeal.x());

        activeLayer.setY((int) mouseIdeal.y());
        break;
      case RESIZE:
        MoveToolUtils.resizeLayer(camera, activeLayer, initialTransform, activeHandle, x, y);
        break;
      case ROTATE:
        float centerX = activeLayer.getX() + activeLayer.getWidth() / 2f;
        float centerY = activeLayer.getY() + activeLayer.getHeight() / 2f;

        Raylib.Vector2 mouseWorld = camera.screenToWorld(x, y);

        float targetAngle =
            (float) Math.toDegrees(Math.atan2(mouseWorld.y() - centerY, mouseWorld.x() - centerX))
                + 90f;

        float closestSnapAngle = Math.round(targetAngle / SNAP_ANGLE_DEGREES) * SNAP_ANGLE_DEGREES;

        if (Math.abs(closestSnapAngle - targetAngle) < SNAP_ANGLE_THRESHOLD)
          targetAngle = closestSnapAngle;

        activeLayer.setRotation(targetAngle);
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
    activeHandle = null;
    activeLayerIndex = -1;
  }

  @Override
  public void onSelected() {}

  @Override
  public Optional<ScreenOverlay> getScreenOverlay() {
    return Optional.of(screenOverlay);
  }
}
