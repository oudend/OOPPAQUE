package se.chalmers.group7.core.tools;

import static com.raylib.Raylib.GetFrameTime;

import se.chalmers.group7.core.operations.OperationManager;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.tools.settings.ToolSettings;
import se.chalmers.group7.core.ui.controller.Input;

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
    previousX = x;
    previousY = y;

    // Save the initial camera position
    startCameraX = camera.getX();
    startCameraY = camera.getY();

    dragging = true;
  }

  public void onPointerMove(int x, int y) {
    if (!dragging) return;

    // Calculate pointer movement
    float deltaX = ((x - previousX) / camera.getZoom());
    float deltaY = ((y - previousY) / camera.getZoom());

    // Adjust camera position
    camera.setX(camera.getX() - deltaX);
    camera.setY(camera.getY() - deltaY);

    // Update previous pointer position
    previousX = x;
    previousY = y;
  }

  public void onPointerUp() {
    if (!dragging) return;

    dragging = false;
    //
    //    // Get final camera position
    //    float endCameraX = camera.getX();
    //    float endCameraY = camera.getY();

    // Create operation
    //    CameraTransformOperation operation =
    //        new CameraTransformOperation(
    //            camera,
    //            startCameraX,
    //            startCameraY,
    //            endCameraX,
    //            endCameraY,
    //            camera.getZoom(),
    //            camera.getZoom(),
    //            true);

    //    operationManager.addOperation(operation);
  }

  @Override
  public void deactivate() {
    dragging = false;
  }

  @Override
  public ToolSettings getSettings() {
    return null;
  }

  @Override
  public void onSelected() {}
}
