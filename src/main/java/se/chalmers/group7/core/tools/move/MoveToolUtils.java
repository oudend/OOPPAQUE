package se.chalmers.group7.core.tools.move;

import com.raylib.Raylib;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerTransform;
import se.chalmers.group7.core.layers.LayerUtils.Handle;
import se.chalmers.group7.core.rendering.Camera;

public class MoveToolUtils {
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
}
