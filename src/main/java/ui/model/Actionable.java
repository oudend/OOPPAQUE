package ui.model;

import ui.controller.Input;

public interface Actionable {
    void evaluateInput(Input input);
    void addAction(Action action);
    void removeAction(Action action);
}
