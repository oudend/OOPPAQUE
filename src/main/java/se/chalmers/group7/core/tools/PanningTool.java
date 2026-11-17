package se.chalmers.group7.core.tools;

import se.chalmers.group7.core.operations.CameraTransformOperation;
import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;

public class PanningTool implements Tool {

  private final Camera camera;
  private final OperationManager operationManager;

  private int previousX;
  private int previousY;

  private float startCameraX;
  private float startCameraY;

  private boolean dragging;

  public PanningTool(Camera camera, OperationManager operationManager) {
    this.camera = camera;
    this.operationManager = operationManager;
  }

  @Override
  public void onPointerDown(int x, int y) {
    previousX = x;
    previousY = y;

    // Save the initial camera position
    startCameraX = camera.getX();
    startCameraY = camera.getY();

    dragging = true;
  }

  @Override
  public void onPointerMove(int x, int y) {
    if (!dragging) return;

    // Calculate pointer movement
    int deltaX = (int) ((x - previousX) / camera.getZoom());
    int deltaY = (int) ((y - previousY) / camera.getZoom());

    // Adjust camera position
    camera.setX(camera.getX() - deltaX);
    camera.setY(camera.getY() - deltaY);

    // Update previous pointer position
    previousX = x;
    previousY = y;
  }

  @Override
  public void onPointerUp() {
    if (!dragging) return;

    dragging = false;

    // Get final camera position
    float endCameraX = camera.getX();
    float endCameraY = camera.getY();

    // Create operation
    CameraTransformOperation operation =
        new CameraTransformOperation(
            camera,
            startCameraX,
            startCameraY,
            endCameraX,
            endCameraY,
            camera.getZoom(),
            camera.getZoom(),
            true);

    operationManager.addOperation(operation);
  }

  @Override
  public void deactivate() {
    dragging = false;
  }
}
