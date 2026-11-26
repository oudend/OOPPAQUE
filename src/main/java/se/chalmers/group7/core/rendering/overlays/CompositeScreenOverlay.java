package se.chalmers.group7.core.rendering.overlays;

import java.util.List;
import se.chalmers.group7.core.rendering.Camera;

public class CompositeScreenOverlay implements ScreenOverlay {
  List<ScreenOverlay> overlays;

  public CompositeScreenOverlay(List<ScreenOverlay> overlays) {
    this.overlays = overlays;
  }

  @Override
  public void render(Camera camera) {
    for (ScreenOverlay overlay : overlays) {
      overlay.render(camera);
    }
  }
}
