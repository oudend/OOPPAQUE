package se.chalmers.group7.core.tools.move;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.Vector2Distance;

import com.raylib.Raylib;
import java.util.ArrayList;
import java.util.List;
import se.chalmers.group7.core.alignment.AlignmentLine;
import se.chalmers.group7.core.alignment.AlignmentUtils;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.rendering.Camera;

class MovingHelper {

  private Vector2 dragStartPosition;
  private Vector2 dragStartMouseWorld;
  private Layer activeLayer;
  private MovingUtils.LineMatch activeMatch;

  List<AlignmentLine> overlayLines = new ArrayList<>();

  private static final float SNAP_ENTER_DISTANCE = 20f;
  private static final float SNAP_EXIT_DISTANCE = 45f;
  private static final float SNAP_SWITCH_THRESHOLD = 25f;
  private static final float SNAP_PREVIEW_DISTANCE = 15f;
  private static final float SNAP_INTERSECTION_DISTANCE = 7.5f;

  public void selectLayer(Camera camera, Layer layer, int x, int y) {
    Raylib.Vector2 mouseWorld = camera.screenToWorld(x, y);

    dragStartPosition = new Vector2().x(layer.getX()).y(layer.getY());
    dragStartMouseWorld = mouseWorld;

    activeLayer = layer;
  }

  private List<MovingUtils.LineMatch> getAllAlignmentMatches(
      Camera camera, List<Layer> layers, Vector2 mouseIdeal) {
    List<Layer> comparisonLayers = new ArrayList<>(layers);
    comparisonLayers.remove(activeLayer);

    List<AlignmentLine> comparisonLines = AlignmentUtils.fromLayers(comparisonLayers);

    List<AlignmentLine> workspaceLines =
        AlignmentUtils.fromRectangle(0, 0, camera.getViewportWidth(), camera.getViewportHeight());

    comparisonLines.addAll(workspaceLines);

    return MovingUtils.getAllAlignmentMatches(activeLayer, comparisonLines, mouseIdeal);
  }

  void move(Camera camera, List<Layer> layers, int x, int y, boolean snappingEnabled) {
    overlayLines.clear();

    Raylib.Vector2 mouseWorld = camera.screenToWorld(x, y);

    Raylib.Vector2 mouseDelta = Vector2Subtract(mouseWorld, dragStartMouseWorld);
    Raylib.Vector2 mouseIdeal = Vector2Add(dragStartPosition, mouseDelta);

    List<MovingUtils.LineMatch> matches = getAllAlignmentMatches(camera, layers, mouseIdeal);

    MovingUtils.AlignedLineMatches alignedLineMatches =
        MovingUtils.alignedLineMatches(camera, matches, mouseIdeal);

    MovingUtils.LineMatch closestMatch = alignedLineMatches.closest();

    MovingUtils.LineMatch oppositeClosestMatch = alignedLineMatches.other();

    Float activeScreenDistance = null;
    Raylib.Vector2 activeSlidingPosition = null;

    if (activeMatch != null) {
      activeSlidingPosition = MovingUtils.getSlidingPosition(mouseIdeal, activeMatch);

      activeScreenDistance =
          Vector2Distance(
              camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y()),
              camera.worldToScreen(activeSlidingPosition.x(), activeSlidingPosition.y()));
    }

    Vector2 closestSlidingPosition = MovingUtils.getSlidingPosition(mouseIdeal, closestMatch);

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
      MovingUtils.LineMatch oppositeActiveMatch =
          activeMatch.type() == closestMatch.type() ? oppositeClosestMatch : closestMatch;

      Vector2 intersectionPosition =
          MovingUtils.getIntersectionPosition(
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

    List<MovingUtils.LineMatch> closeMatches =
        MovingUtils.findCloseMatches(
            camera,
            matches,
            mouseIdeal,
            activeMatch == null
                ? SNAP_ENTER_DISTANCE + SNAP_PREVIEW_DISTANCE
                : SNAP_EXIT_DISTANCE + SNAP_PREVIEW_DISTANCE);

    if (!closeMatches.isEmpty() && snappingEnabled) {
      List<AlignmentLine> comparisonLines =
          closeMatches.stream().map(MovingUtils.LineMatch::comparisonLine).toList();

      overlayLines.addAll(comparisonLines);
    }

    if (activeMatch != null && snappingEnabled) {
      activeLayer.setX((int) activeSlidingPosition.x());

      activeLayer.setY((int) activeSlidingPosition.y());

      return;
    }

    activeLayer.setX((int) mouseIdeal.x());

    activeLayer.setY((int) mouseIdeal.y());
  }

  public List<AlignmentLine> getOverlayLines() {
    return overlayLines;
  }
}
