package se.chalmers.group7.core.ui.model;

import se.chalmers.group7.core.ui.controller.Input;

public abstract class MouseAction implements Action {
  public void executeAction(Input input) {
    if (input.mouseLeft().down()) {
      onMouseLeftDown();
    }
    if (input.mouseRight().down()) {
      onMouseRightDown();
    }
    if (input.mouseMiddle().down()) {
      onMouseMiddleDown();
    }

    if (input.mouseLeft().hold()) {
      onMouseLeftHold();
    }
    if (input.mouseRight().hold()) {
      onMouseRightHold();
    }
    if (input.mouseMiddle().hold()) {
      onMouseMiddleHold();
    }

    if (input.mouseLeft().released()) {
      onMouseLeftUp();
    }
    if (input.mouseRight().released()) {
      onMouseRightUp();
    }
    if (input.mouseMiddle().released()) {
      onMouseMiddleUp();
    }
  }

  protected void onMouseLeftDown() {}

  protected void onMouseRightDown() {}

  protected void onMouseMiddleDown() {}

  protected void onMouseLeftHold() {}

  protected void onMouseRightHold() {}

  protected void onMouseMiddleHold() {}

  protected void onMouseLeftUp() {}

  protected void onMouseRightUp() {}

  protected void onMouseMiddleUp() {}
}
