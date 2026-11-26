package se.chalmers.group7.core.ui.model;

import java.util.List;
import se.chalmers.group7.core.ui.controller.Input;

public abstract class LayoutBlock implements Actionable {
  protected int width;
  protected int height;
  protected int desiredWidth;
  protected int desiredHeight;

  protected List<Action> actions;

  public void evaluateInput(Input input) {
    for (Action a : actions) {
      a.executeAction(input);
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

  public void draw() {
    draw(0, 0);
  }

  public abstract void draw(int xPos, int yPos);
}
