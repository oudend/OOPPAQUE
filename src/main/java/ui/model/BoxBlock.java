package ui.model;

import static com.raylib.Raylib.*;

import com.raylib.Raylib.*;
import java.util.List;
import ui.controller.Input;

public class BoxBlock extends LayoutBlock {
  List<LayoutBlock> children;
  BoxProperties properties;

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

    input =
        input.enterArea(
            properties.getMarginX() + properties.getBorderSize(),
            properties.getMarginY() + properties.getBorderSize());

    if (!children.isEmpty()) {
      int position = getInputPos(input);
      int left = 0;
      int right = children.size() - 1;
      int resultIndex = -1;

      while (left <= right) {
        int middle = left + (right - left) / 2;
        int childPos = getDirectionPos(children.get(middle));

        if (childPos < position) {
          resultIndex = middle;
          left = middle + 1;
        } else {
          right = middle - 1;
        }
      }

      if (resultIndex != -1) {
        LayoutBlock child = children.get(resultIndex);
        child.evaluateInput(input.enterArea(child.getXPos(), child.getYPos()));
      }
    }
  }

  private int getInputPos(Input input) {
    if (this.properties.getDirection() == Direction.row) {
      return input.xPos();
    } else if (this.properties.getDirection() == Direction.column) {
      return input.yPos();
    }
    return -1;
  }

  private int getDirectionPos(LayoutBlock block) {
    if (this.properties.getDirection() == Direction.row) {
      return block.getXPos();
    } else if (this.properties.getDirection() == Direction.column) {
      return block.getYPos();
    }
    return -1;
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

    int position = (properties.getDirection() == Direction.row) ? topLeftX : topLeftY;
    for (LayoutBlock c : children) {
      int size = (properties.getDirection() == Direction.row) ? c.getWidth() : c.getHeight();

      int x = (properties.getDirection() == Direction.row) ? position : topLeftX;
      int y = (properties.getDirection() == Direction.row) ? topLeftY : position;

      BeginScissorMode(x, y, c.getWidth(), c.getHeight());
      c.draw(x, y);
      EndScissorMode();

      position += size;
    }
  }
}
