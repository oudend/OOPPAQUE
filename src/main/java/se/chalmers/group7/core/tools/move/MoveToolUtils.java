package se.chalmers.group7.core.tools.move;

import static com.raylib.Raylib.*;

import com.raylib.Raylib;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import se.chalmers.group7.core.alignment.AlignmentLine;
import se.chalmers.group7.core.alignment.AlignmentUtils;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerManager;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.layers.LayerUtils.Handle;
import se.chalmers.group7.core.rendering.Camera;

class MoveToolUtils {
  public static void resizeLayer(
      Camera camera,
      Layer layer,
      LayerTransform initialTransform,
      Handle activeHandle,
      int mouseX,
      int mouseY) {
    Raylib.Vector2 mouseWorld = camera.screenToWorld(mouseX, mouseY);
    Raylib.Vector2 handleWorld = activeHandle.worldCoords();

    float initX = initialTransform.x();
    float initY = initialTransform.y();
    float initW = initialTransform.width();
    float initH = initialTransform.height();

    // Rotation pivot (center of the layer)
    float pivotX = initX + initW / 2f;
    float pivotY = initY + initH / 2f;

    double angleRad = Math.toRadians(layer.getRotation());
    float cos = (float) Math.cos(angleRad);
    float sin = (float) Math.sin(angleRad);

    // Transform world coordinates into the layer's local space (inverse rotation)
    float mdx = mouseWorld.x() - pivotX;
    float mdy = mouseWorld.y() - pivotY;
    float mouseLocalX = mdx * cos + mdy * sin;
    float mouseLocalY = -mdx * sin + mdy * cos;

    float hdx = handleWorld.x() - pivotX;
    float hdy = handleWorld.y() - pivotY;
    float handleLocalX = hdx * cos + hdy * sin;
    float handleLocalY = -hdx * sin + hdy * cos;

    // Initial bounds in local space (relative to the center pivot)
    float left = -initW / 2f;
    float right = initW / 2f;
    float top = -initH / 2f;
    float bottom = initH / 2f;

    boolean moveLeft = Math.abs(handleLocalX - left) < Math.abs(handleLocalX - right);
    boolean moveTop = Math.abs(handleLocalY - top) < Math.abs(handleLocalY - bottom);

    float minWidth = 10f;
    float minHeight = 10f;

    switch (activeHandle.type()) {
      case SCALE_WIDTH, SCALE_CORNER -> {
        if (moveLeft) {
          left = Math.min(mouseLocalX, right - minWidth);
        } else {
          right = Math.max(mouseLocalX, left + minWidth);
        }
      }
      default -> {}
    }

    switch (activeHandle.type()) {
      case SCALE_HEIGHT, SCALE_CORNER -> {
        if (moveTop) {
          top = Math.min(mouseLocalY, bottom - minHeight);
        } else {
          bottom = Math.max(mouseLocalY, top + minHeight);
        }
      }
      default -> {}
    }

    float newWidth = right - left;
    float newHeight = bottom - top;

    // Find the new center in local space and rotate it back to world space
    float localCenterX = (left + right) / 2f;
    float localCenterY = (top + bottom) / 2f;
    float newCenterX = pivotX + (localCenterX * cos - localCenterY * sin);
    float newCenterY = pivotY + (localCenterX * sin + localCenterY * cos);

    layer.setX(Math.round(newCenterX - newWidth / 2f));
    layer.setY(Math.round(newCenterY - newHeight / 2f));
    layer.setWidth(Math.round(newWidth));
    layer.setHeight(Math.round(newHeight));
  }

  record MainLine(Layer layer, int index, float angle) {
    public AlignmentLine alignmentLine(Vector2 mouseIdeal) {

      LayerUtils.BoundingBox mainBox =
          LayerUtils.getBoundingBox(
              mouseIdeal.x(),
              mouseIdeal.y(),
              layer.getWidth(),
              layer.getHeight(),
              layer.getRotation());

      List<AlignmentLine> mainLines =
          AlignmentUtils.fromRectangle(
              mainBox.x(), mainBox.y(), mainBox.width(), mainBox.height(), 0f);

      //      List<AlignmentLine> mainLines = AlignmentUtils.fromRectangle(
      //              mouseIdeal.x(), mouseIdeal.y(), layer.getWidth(), layer.getHeight(), angle);

      return mainLines.get(index);
    }
  }

  record LineMatch(MainLine mainLine, AlignmentLine comparisonLine, float distance) {}

  public static Vector2 getSlidingPosition(Vector2 mouseIdeal, MoveToolUtils.LineMatch match) {
    AlignmentLine main = match.mainLine().alignmentLine(mouseIdeal);
    AlignmentLine comparison = match.comparisonLine();

    double angle = Math.toRadians(main.angle());
    float nx = (float) -Math.sin(angle);
    float ny = (float) Math.cos(angle);

    float dx = comparison.x() - main.x();
    float dy = comparison.y() - main.y();

    // Signed distance along normal from main -> comparison
    float signedPerpDist = dx * nx + dy * ny;

    return new Vector2()
        .x(mouseIdeal.x() + nx * signedPerpDist)
        .y(mouseIdeal.y() + ny * signedPerpDist);
  }

  public static List<LineMatch> findCloseMatches(
      Camera camera, List<MoveToolUtils.LineMatch> matches, Vector2 mouseIdeal, float minDistance) {

    return matches.stream()
        .filter(match -> {
          Vector2 slidingPosition = getSlidingPosition(mouseIdeal, (LineMatch) match);

          Vector2 mouseScreen = camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y());

          Vector2 slidingScreen = camera.worldToScreen(slidingPosition.x(), slidingPosition.y());

          return Vector2Distance(mouseScreen, slidingScreen) < minDistance;
        })
        .toList();
  }

  public static LineMatch findClosestMatchToMouse(
      Camera camera, List<MoveToolUtils.LineMatch> matches, Vector2 mouseIdeal) {

    return matches.stream()
        .min(
            Comparator.comparingDouble(match -> {
              Vector2 slidingPosition = getSlidingPosition(mouseIdeal, match);

              Vector2 mouseScreen = camera.worldToScreen(mouseIdeal.x(), mouseIdeal.y());

              Vector2 slidingScreen =
                  camera.worldToScreen(slidingPosition.x(), slidingPosition.y());

              return Vector2Distance(mouseScreen, slidingScreen);
            }))
        .orElse(null);
  }

  public static List<LineMatch> getAllAlignmentMatches(
      Camera camera, LayerManager layerManager, Vector2 mouseIdeal) {
    List<Layer> comparisonLayers = new ArrayList<>(layerManager.getLayers());
    comparisonLayers.remove(layerManager.getActiveLayerIndex());

    List<AlignmentLine> comparisonLines = AlignmentUtils.fromLayers(comparisonLayers);

    List<AlignmentLine> workspaceLines =
        AlignmentUtils.fromRectangle(
            0, 0, camera.getViewportWidth(), camera.getViewportHeight(), 0);

    comparisonLines.addAll(workspaceLines);

    Layer activeLayer = layerManager.getActiveLayer();

    return MoveToolUtils.getAllAlignmentMatches(activeLayer, comparisonLines, mouseIdeal);
  }

  public static List<LineMatch> getAllAlignmentMatches(
      Layer mainLayer, List<AlignmentLine> comparisonLines, Vector2 mouseIdeal) {
    List<LineMatch> matches = new ArrayList<>();

    LayerUtils.BoundingBox mainBox =
        LayerUtils.getBoundingBox(
            mouseIdeal.x(),
            mouseIdeal.y(),
            mainLayer.getWidth(),
            mainLayer.getHeight(),
            mainLayer.getRotation());

    List<AlignmentLine> mainLines =
        AlignmentUtils.fromRectangle(
            mainBox.x(), mainBox.y(), mainBox.width(), mainBox.height(), 0f);

    for (int i = 0; i < mainLines.size(); i++) {
      AlignmentLine mLine = mainLines.get(i);

      for (AlignmentLine cLine : comparisonLines) {

        if (AlignmentUtils.angularDifference(mLine.angle(), cLine.angle()) > 0.001f) {
          continue;
        }

        float distance = AlignmentUtils.perpendicularDistance(mLine, cLine);

        MainLine mainLine = new MainLine(mainLayer, i, mLine.angle());

        if (isHorizontal(mLine)) {
          matches.add(new LineMatch(mainLine, cLine, distance));
        } else {
          matches.add(new LineMatch(mainLine, cLine, distance));
        }
      }
    }

    return matches;
  }

  private static boolean isHorizontal(AlignmentLine line) {
    float angle = Math.abs(line.angle()) % 180f;
    return angle < 45f || angle > 135f;
  }
}
