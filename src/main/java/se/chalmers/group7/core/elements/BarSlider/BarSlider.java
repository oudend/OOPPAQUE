package se.chalmers.group7.core.elements.BarSlider;

import static com.raylib.Colors.BLACK;
import static com.raylib.Colors.WHITE;
import static com.raylib.Raylib.DrawRectangle;
import static com.raylib.Raylib.DrawRectangleLinesEx;
import static com.raylib.Raylib.DrawRectangleRec;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;

import com.raylib.Raylib.Rectangle;
import java.util.ArrayList;
import se.chalmers.group7.core.ui.controller.Input;
import se.chalmers.group7.core.ui.model.LayoutBlock;

public class BarSlider extends LayoutBlock {
  int topLeftX = 0;
  int topLeftY = 0;
  int addPosX = 0;

  float min = 0;
  float max = 1;
  float value = min;

  float divide;

  Rectangle sliderButton;

  public BarSlider(int width, int height, int xPos, int yPos, float min, float max, float value) {
    this.width = width;
    this.height = height;
    this.min = min;
    this.max = max;
    this.value = value;

    sliderButton = new Rectangle().x(xPos).y(yPos).width(30).height(height);
    this.divide = (float) (width - (int) sliderButton.width()) / max;
    this.actions = new ArrayList<>();
  }

  public void draw(int xPos, int yPos) {
    int topLeftX = xPos;
    int topLeftY = yPos;

    int width = this.width;
    int height = this.height;
    int currPos = topLeftX + addPosX;

    DrawRectangle(topLeftX, topLeftY, width, height, BLACK);
    DrawRectangleRec(sliderButton.x(currPos).y(topLeftY), WHITE);
    DrawRectangleLinesEx(
        new Rectangle().x(topLeftX).y(topLeftY).width(width).height(height), 1, WHITE);
  }

  public void moveSliderBar(Input input) {
    if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
      addPosX =
          Math.clamp((int) input.xPos(), topLeftX, topLeftX + width - (int) sliderButton.width());
      value = (float) addPosX / divide;
      System.out.println(value);
    }
  }
}
