package se.chalmers.group7.core.canvas;

import com.raylib.Raylib;

public interface Canvas {
  int getWidth();

  int getHeight();

  void dispose();

  void clear();

  Canvas clone();

  Raylib.Texture getTexture();

  void drawCircle(int x, int y, float radius, Raylib.Color color, BlendingMode blendingMode);

  void drawLine(
      int startX,
      int startY,
      int endX,
      int endY,
      float diameter,
      Raylib.Color color,
      BlendingMode blendingMode);

  void drawTexture(
      Raylib.Texture texture,
      float x,
      float y,
      float width,
      float height,
      BlendingMode blendingMode);
}
