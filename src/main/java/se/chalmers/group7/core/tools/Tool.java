package se.chalmers.group7.core.tools;

import java.util.Optional;
import se.chalmers.group7.core.rendering.LayerPreview;
import se.chalmers.group7.core.rendering.ScreenOverlay;
import se.chalmers.group7.core.ui.controller.Input;

public interface Tool {
  void onInput(Input input);

  void onSelected();

  void deactivate();

  default Optional<LayerPreview> getLayerPreview() {
    return Optional.empty();
  }

  default Optional<ScreenOverlay> getScreenOverlay() {
    return Optional.empty();
  }
}
