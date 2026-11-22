package se.chalmers.group7.core.operations;

import se.chalmers.group7.core.layers.Layer;
import se.chalmers.group7.core.layers.LayerTransform;

public class LayerTransformOperation extends Operation {

  private final Layer layer;

  private final LayerTransform oldTransform;
  private final LayerTransform newTransform;

  public LayerTransformOperation(
      Layer layer, LayerTransform oldTransform, LayerTransform newTransform, boolean executed) {

    super(executed);

    this.layer = layer;
    this.oldTransform = oldTransform;
    this.newTransform = newTransform;
  }

  @Override
  public void redo() {
    super.redo();
    layer.setLayerTransform(newTransform);
  }

  @Override
  public void undo() {
    super.undo();
    layer.setLayerTransform(oldTransform);
  }
}
