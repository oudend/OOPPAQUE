package se.chalmers.group7.core.layers;

public record LayerTransform(int x, int y, int width, int height, float rotation) {
  public static LayerTransform from(Layer layer) {
    return new LayerTransform(
        layer.getX(), layer.getY(), layer.getWidth(), layer.getHeight(), layer.getRotation());
  }
}
