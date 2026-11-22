package se.chalmers.group7.core.ui.model;

import static com.raylib.Raylib.*;

import java.util.ArrayList;
import java.util.List;
import se.chalmers.group7.core.ui.controller.Input;

public class BoxBlock extends LayoutBlock {
  List<LayoutBlock> children;
  BoxProperties properties;

  public BoxBlock(BoxProperties properties, int width, int height) {
    super();

    children = new ArrayList<>();
    this.properties = properties;
    this.width = width;
    this.height = height;
    this.actions = new ArrayList<>();
  }

  @Override
  public void evaluateInput(Input input) {
    if (input.xPos() < properties.getMarginX()
        || input.yPos() < properties.getMarginY()
        || input.xPos() >= width - properties.getMarginX()
        || input.yPos() >= height - properties.getMarginY()) return;

    super.evaluateInput(input);

    if (input.xPos() < properties.getMarginX() + properties.getBorderSize()
        || input.yPos() < properties.getMarginY() + properties.getBorderSize()
        || input.xPos() >= width - properties.getMarginX() - properties.getBorderSize()
        || input.yPos() >= height - properties.getMarginY() - properties.getBorderSize()) return;

    if (!children.isEmpty()) {
      int position = getInputPos(input);
      int left = 0;
      int right = children.size() - 1;
      int resultIndex = -1;

      List<List<Integer>> childPositions = getChildPositions();

      while (left <= right) {
        int middle = left + (right - left) / 2;
        int childPos =
            childPositions.get(middle).get(properties.getDirection() == Direction.row ? 0 : 1);

        if (childPos <= position) {
          resultIndex = middle;
          left = middle + 1;
        } else {
          right = middle - 1;
        }
      }

      if (resultIndex != -1) {
        LayoutBlock child = children.get(resultIndex);
        child.evaluateInput(
            input.enterArea(
                childPositions.get(resultIndex).get(0)
                    + properties.getMarginX()
                    + properties.getPaddingX()
                    + properties.getBorderSize(),
                childPositions.get(resultIndex).get(1)
                    + properties.getMarginY()
                    + properties.getPaddingY()
                    + properties.getBorderSize()));
      }
    }
  }

  private int getInputPos(Input input) {
    if (properties.getDirection() == Direction.row) {
      return input.xPos()
          - properties.getMarginX()
          - properties.getBorderSize()
          - properties.getPaddingX();
    } else {
      return input.yPos()
          - properties.getMarginY()
          - properties.getBorderSize()
          - properties.getPaddingY();
    }
  }

  private List<List<Integer>> getChildPositions() {
    List<List<Integer>> positions = new ArrayList<>();

    int currentX = 0;
    int currentY = 0;

    for (LayoutBlock child : children) {
      positions.add(List.of(currentX, currentY));

      if (properties.getDirection() == Direction.row) {
        currentX += child.getWidth();
      } else {
        currentY += child.getHeight();
      }
    }

    return positions;
  }

  public void draw(int xPos, int yPos) {
    int topLeftX = xPos + properties.getMarginX();
    int topLeftY = yPos + properties.getMarginY();
    int width = this.width - 2 * properties.getMarginX();
    int height = this.height - 2 * properties.getMarginY();

    DrawRectangle(topLeftX, topLeftY, width, height, properties.getColor());

    DrawRectangleLinesEx(
        new Rectangle().x(topLeftX).y(topLeftY).width(width).height(height),
        properties.getBorderSize(),
        properties.getBorderColor());

    int border = properties.getBorderSize();

    topLeftX += border + properties.getPaddingX();
    width -= 2 * (border + properties.getPaddingX());
    topLeftY += border + properties.getPaddingY();
    height -= 2 * (border + properties.getPaddingY());

    int position = (properties.getDirection() == Direction.row) ? topLeftX : topLeftY;
    for (LayoutBlock c : children) {
      int size = (properties.getDirection() == Direction.row) ? c.getWidth() : c.getHeight();

      int x = (properties.getDirection() == Direction.row) ? position : topLeftX;
      int y = (properties.getDirection() == Direction.row) ? topLeftY : position;

      BeginScissorMode(
          x,
          y,
          Math.min(c.getWidth(), Math.max(0, topLeftX + width - x)),
          Math.min(c.getHeight(), Math.max(0, topLeftY + height - y)));
      c.draw(x, y);
      EndScissorMode();

      position += size;
    }
  }

  public void addChild(LayoutBlock child) {
    children.add(child);
  }

  public void removeChild(LayoutBlock child) {
    children.remove(child);
  }
}
