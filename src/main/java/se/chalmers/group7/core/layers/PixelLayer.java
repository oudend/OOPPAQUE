package se.chalmers.group7.core.layers;

import se.chalmers.group7.core.canvas.Canvas;
import se.chalmers.group7.core.canvas.RaylibCanvas;

public class PixelLayer implements Layer {
  private Canvas canvas;
  private int x;
  private int y;
  private int width;
  private int height;
  private float rotation;
  private final LayerSettings layerSettings;

  public PixelLayer(Canvas canvas, int x, int y, int width, int height, float rotation) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.rotation = rotation;
    this.canvas = canvas;

    this.layerSettings = new LayerSettings();
  }

  public PixelLayer(int width, int height) {
    this(new RaylibCanvas(width, height), 0, 0, width, height, 0);
  }

  @Override
  public Canvas getCanvas() {
    return this.canvas;
  }

  @Override
  public int getX() {
    return this.x;
  }

  @Override
  public int getY() {
    return this.y;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public float getRotation() {
    return this.rotation;
  }

  @Override
  public LayerSettings getLayerSettings() {
    return this.layerSettings;
  }

  @Override
  public LayerTransform getLayerTransform() {
    return new LayerTransform(x, y, width, height, rotation);
  }

  @Override
  public void setLayerTransform(LayerTransform layerTransform) {
    this.x = layerTransform.x();
    this.y = layerTransform.y();
    this.width = layerTransform.width();
    this.height = layerTransform.height();
    this.rotation = layerTransform.rotation();
  }
}
