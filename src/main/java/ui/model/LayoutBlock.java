package ui.model;

import java.util.List;
import ui.controller.Input;

public abstract class LayoutBlock implements Actionable {
  int xPos;
  int yPos;
  int width;
  int height;
  int desiredWidth;
  int desiredHeight;

  List<Action> actions;

  public void evaluateInput(Input input) {
    for (Action a : actions) {
      a.executeAction(input.enterArea(xPos, yPos));
    }
  }

  public void addAction(Action action) {
    actions.add(action);
  }

  public void removeAction(Action action) {
    actions.remove(action);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getDesiredWidth() {
    return desiredWidth;
  }

  public int getDesiredHeight() {
    return desiredHeight;
  }

  public int getXPos() {
    return xPos;
  }

  public int getYPos() {
    return yPos;
  }
}
