package se.chalmers.group7.core.layers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import se.chalmers.group7.core.canvas.Canvas;

public class LayerManager {
  private final List<Layer> layers = new ArrayList<>();
  private int activeLayerIndex = 0;

  public LayerManager() {}

  public int getActiveLayerIndex() {
    return this.activeLayerIndex;
  }

  public Layer getActiveLayer() {
    return layers.get(this.activeLayerIndex);
  }

  public void setActiveLayer(int activeLayer) {
    this.activeLayerIndex = activeLayer;
  }

  public List<Layer> getLayers() {
    return layers;
  }

  public void swapLayers(int i, int j) {
    Collections.swap(layers, i, j);
  }

  public Layer getLayer(int index) {
    return layers.get(index);
  }

  public int getLayerCount() {
    return layers.size();
  }

  public int addLayer(Layer layer) {
    layers.add(layer);
    return layers.size() - 1;
  }

  public Canvas getActiveCanvas() {
    return getActiveLayer().getCanvas();
  }
}
