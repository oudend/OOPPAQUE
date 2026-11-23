package se.chalmers.group7.core.layers;

import se.chalmers.group7.core.canvas.Canvas;

public interface Layer {
  Canvas getCanvas();

  int getX();

  void setX(int x);

  int getY();

  void setY(int y);

  int getWidth();

  void setWidth(int width);

  int getHeight();

  void setHeight(int height);

  float getRotation();

  void setRotation(float rotation);

  void setLayerTransform(LayerTransform layerTransform);

  LayerTransform getLayerTransform();

  LayerSettings getLayerSettings();
}
