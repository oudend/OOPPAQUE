package se.chalmers.group7.core.tools;

// mouse events?
// void onMouseEvent(MouseEvent);
// void onSelect();
// void onDeactivate();

import se.chalmers.group7.core.ui.controller.Input;

public interface Tool {
  void onInput(Input input);

  void onSelected();

  void deactivate();
}
