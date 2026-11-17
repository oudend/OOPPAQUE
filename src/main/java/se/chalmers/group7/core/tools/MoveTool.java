package se.chalmers.group7.core.tools;

import se.chalmers.group7.core.rendering.Camera;

public class MoveTool implements Tool {

  private final Camera camera;

  public MoveTool(Camera camera) {
    this.camera = camera;
  }

  @Override
  public void onPointerDown(int x, int y) {}

  @Override
  public void onPointerMove(int x, int y) {}

  @Override
  public void onPointerUp() {}

  @Override
  public void deactivate() {}
}
