package ui.model;

import ui.controller.Input;

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

  void onMouseLeftDown() {}

  void onMouseRightDown() {}

  void onMouseMiddleDown() {}

  void onMouseLeftHold() {}

  void onMouseRightHold() {}

  void onMouseMiddleHold() {}

  void onMouseLeftUp() {}

  void onMouseRightUp() {}

  void onMouseMiddleUp() {}
}
