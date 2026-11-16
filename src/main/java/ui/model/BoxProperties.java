package ui.model;

import com.raylib.Raylib.Color;

public class BoxProperties {
  private Direction direction;

  private Color color;

  private int borderSize;
  private Color borderColor;

  private int marginX;
  private int marginY;

  private int paddingX;
  private int paddingY;

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public int getBorderSize() {
    return borderSize;
  }

  public void setBorderSize(int borderSize) {
    this.borderSize = borderSize;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public int getMarginX() {
    return marginX;
  }

  public void setMarginX(int marginX) {
    this.marginX = marginX;
  }

  public int getMarginY() {
    return marginY;
  }

  public void setMarginY(int marginY) {
    this.marginY = marginY;
  }

  public int getPaddingX() {
    return paddingX;
  }

  public void setPaddingX(int paddingX) {
    this.paddingX = paddingX;
  }

  public int getPaddingY() {
    return paddingY;
  }

  public void setPaddingY(int paddingY) {
    this.paddingY = paddingY;
  }
}
