package se.chalmers.group7.core.tools.move;

import com.raylib.Raylib;
import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.rendering.Camera;

class RotationHelper {

  private static final float SNAP_ANGLE_DEGREES = 90f;
  private static final float SNAP_ANGLE_THRESHOLD = 10f;

  static void rotate(Camera camera, Layer layer, int x, int y) {
    float centerX = layer.getX() + layer.getWidth() / 2f;
    float centerY = layer.getY() + layer.getHeight() / 2f;

    Raylib.Vector2 mouseWorld = camera.screenToWorld(x, y);

    float targetAngle =
        (float) Math.toDegrees(Math.atan2(mouseWorld.y() - centerY, mouseWorld.x() - centerX))
            + 90f;

    float closestSnapAngle = Math.round(targetAngle / SNAP_ANGLE_DEGREES) * SNAP_ANGLE_DEGREES;

    if (Math.abs(closestSnapAngle - targetAngle) < SNAP_ANGLE_THRESHOLD)
      targetAngle = closestSnapAngle;

    layer.setRotation(targetAngle);
  }
}
