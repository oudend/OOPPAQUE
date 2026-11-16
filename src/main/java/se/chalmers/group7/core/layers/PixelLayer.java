package se.chalmers.group7.core.layers;

import se.chalmers.group7.core.Canvas;

public class PixelLayer implements Layer {
  private Canvas canvas;
  private int x;
  private int y;
  private int width;
  private int height;
  private float rotation;

  public PixelLayer(Canvas canvas, int x, int y, int width, int height, float rotation) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.rotation = rotation;
    this.canvas = canvas;
  }

  public PixelLayer(Canvas canvas, int width, int height) {
    this(canvas, 0, 0, width, height, 0);
  }

  @Override
  public Canvas getCanvas() {
    return null;
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
}
