package se.chalmers.group7.core.operations;

import se.chalmers.group7.core.rendering.Camera;

public class CameraTransformOperation extends Operation {

  private final Camera camera;

  private final float oldX;
  private final float oldY;

  private final float newX;
  private final float newY;
  private final float oldZoom;
  private final float newZoom;

  public CameraTransformOperation(
      Camera camera,
      float oldX,
      float oldY,
      float newX,
      float newY,
      float oldZoom,
      float newZoom,
      boolean executed) {
    super(executed);

    this.camera = camera;

    this.oldX = oldX;
    this.oldY = oldY;

    this.newX = newX;
    this.newY = newY;
    this.oldZoom = oldZoom;
    this.newZoom = newZoom;
  }

  @Override
  public void redo() {
    super.redo();

    camera.setX(newX);
    camera.setY(newY);
    camera.setZoom(newZoom);
  }

  @Override
  public void undo() {
    super.undo();

    camera.setX(oldX);
    camera.setY(oldY);
    camera.setZoom(oldZoom);
  }
}
