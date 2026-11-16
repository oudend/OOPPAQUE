package ui.view;

import java.util.List;
import ui.model.LayoutLayer;

public class LayoutComposer {
  List<LayoutLayer> layers;

  public void addLayer(LayoutLayer layer) {
    layers.add(layer);
  }

  public void removeLayer(LayoutLayer layer) {
    layers.remove(layer);
  }

  public void draw() {
    for (LayoutLayer l : layers) {
      l.draw();
    }
  }
}
