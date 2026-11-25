package se.chalmers.group7.core.ui.model;

import static com.raylib.Colors.BLACK;
import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawRectangleLinesEx;
import static com.raylib.Raylib.DrawRectangleRec;

import com.raylib.Raylib.Rectangle;

public class Slider extends LayoutBlock {

  float min = 0;
  float max = 1;
  float value = min;

  Rectangle sliderButton;

  public Slider(int width, int height, int xPos, int yPos, float min, float max, float value) {
    this.width = width;
    this.height = height;
    this.xPos = xPos;
    this.yPos = yPos;
    this.min = min;
    this.max = max;
    this.value = value;

    sliderButton = new Rectangle().x(xPos).y(yPos).width(30).height(height);
  }

  public void draw(int xPos, int yPos) {
    int topLeftX = xPos;
    int topLeftY = yPos;
    int width = this.width;
    int height = this.height;

    DrawRectangle(topLeftX, topLeftY, width, height, BLACK);
    DrawRectangleRec(sliderButton.x(topLeftX).y(topLeftY), WHITE);
    DrawRectangleLinesEx(
        new Rectangle().x(topLeftX).y(topLeftY).width(width).height(height), 1, WHITE);
  }
}
