package se.chalmers.group7.core.tools.move;

import com.raylib.Raylib;
import java.util.List;
import se.chalmers.group7.core.alignment.AlignmentLine;
import se.chalmers.group7.core.alignment.AlignmentLineType;
import se.chalmers.group7.core.alignment.AlignmentUtils;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.layers.LayerUtils;
import se.chalmers.group7.core.rendering.Camera;

class RotationUtils {

  private static Raylib.Vector2 snapPointToLayerLines(
      Camera camera,
      List<Layer> layers,
      List<AlignmentLine> overlayLines,
      Raylib.Vector2 point,
      float screenDistanceThreshold) {

    List<AlignmentLine> alignmentLines = AlignmentUtils.fromLayers(layers);

    Raylib.Vector2 screenPoint = camera.worldToScreen(point.x(), point.y());

    AlignmentLine closestHorizontalLine =
        AlignmentUtils.closestLine(
            point,
            alignmentLines.stream()
                .filter(match -> match.type() == AlignmentLineType.HORIZONTAL)
                .toList());
    AlignmentLine closestVerticalLine =
        AlignmentUtils.closestLine(
            point,
            alignmentLines.stream()
                .filter(match -> match.type() == AlignmentLineType.VERTICAL)
                .toList());

    Raylib.Vector2 closestHorizontalPoint =
        AlignmentUtils.closestPointOnLine(point, closestHorizontalLine);
    Raylib.Vector2 closestVerticalPoint =
        AlignmentUtils.closestPointOnLine(point, closestVerticalLine);

    overlayLines.add(closestHorizontalLine);
    overlayLines.add(closestVerticalLine);

    float horizontalDistance =
        Raylib.Vector2Distance(
            screenPoint,
            camera.worldToScreen(closestHorizontalPoint.x(), closestHorizontalPoint.y()));
    float verticalDistance =
        Raylib.Vector2Distance(
            screenPoint, camera.worldToScreen(closestVerticalPoint.x(), closestVerticalPoint.y()));

    //    overlayLines.ad

    Raylib.Vector2 closestPoint =
        horizontalDistance < verticalDistance ? closestHorizontalPoint : closestVerticalPoint;
    float closestDistance = Math.min(horizontalDistance, verticalDistance);

    Raylib.Vector2 cornerPoint =
        new Raylib.Vector2().x(closestVerticalPoint.x()).y(closestHorizontalPoint.y());
    float cornerDistance =
        Raylib.Vector2Distance(screenPoint, camera.worldToScreen(cornerPoint.x(), cornerPoint.y()));

    Raylib.Vector2 snapPoint = point;

    if (closestDistance < screenDistanceThreshold) snapPoint = closestPoint;

    if (cornerDistance < screenDistanceThreshold) snapPoint = cornerPoint;

    return snapPoint;
  }

  public static void resizeLayer(
      Camera camera,
      Layer layer,
      List<Layer> layers,
      LayerTransform initialTransform,
      LayerUtils.Handle activeHandle,
      List<AlignmentLine> overlayLines,
      int mouseX,
      int mouseY) {
    Raylib.Vector2 mouseWorld = camera.screenToWorld(mouseX, mouseY);
    Raylib.Vector2 handleWorld = activeHandle.worldCoords();

    mouseWorld =
        snapPointToLayerLines(
            camera,
            layers.stream().filter(l -> l != layer).toList(),
            overlayLines,
            mouseWorld,
            10f);

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

    int newX = Math.round(newCenterX - newWidth / 2f);
    int newY = Math.round(newCenterY - newHeight / 2f);

    layer.setX(newX);
    layer.setY(newY);
    layer.setWidth(Math.round(newWidth));
    layer.setHeight(Math.round(newHeight));
  }
}
