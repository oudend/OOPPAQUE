package se.chalmers.group7.core.layers;

import se.chalmers.group7.core.canvas.Canvas;

public interface Layer {
  Canvas getCanvas();

  int getX();

  int getY();

  int getWidth();

  int getHeight();

  float getRotation();

  LayerSettings getLayerSettings();
}
