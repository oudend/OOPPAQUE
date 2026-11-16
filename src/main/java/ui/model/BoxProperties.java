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

  public BoxProperties setDirection(Direction direction) {
    this.direction = direction;
    return this;
  }

  public Color getColor() {
    return color;
  }

  public BoxProperties setColor(Color color) {
    this.color = color;
    return this;
  }

  public int getBorderSize() {
    return borderSize;
  }

  public BoxProperties setBorderSize(int borderSize) {
    this.borderSize = borderSize;
    return this;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public BoxProperties setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
    return this;
  }

  public int getMarginX() {
    return marginX;
  }

  public BoxProperties setMarginX(int marginX) {
    this.marginX = marginX;
    return this;
  }

  public int getMarginY() {
    return marginY;
  }

  public BoxProperties setMarginY(int marginY) {
    this.marginY = marginY;
    return this;
  }

  public int getPaddingX() {
    return paddingX;
  }

  public BoxProperties setPaddingX(int paddingX) {
    this.paddingX = paddingX;
    return this;
  }

  public int getPaddingY() {
    return paddingY;
  }

  public BoxProperties setPaddingY(int paddingY) {
    this.paddingY = paddingY;
    return this;
  }
}
