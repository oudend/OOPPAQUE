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
}
