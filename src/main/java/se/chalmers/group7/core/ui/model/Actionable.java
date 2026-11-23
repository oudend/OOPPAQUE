package se.chalmers.group7.core.ui.model;

import se.chalmers.group7.core.ui.controller.Input;

public interface Actionable {
  void evaluateInput(Input input);

  void addAction(Action action);

  void removeAction(Action action);
}
