package ui.model;

import ui.controller.Input;

import java.util.List;

public abstract class LayoutBlock implements Actionable {
    int width;
    int height;
    int desiredWidth;
    int desiredHeight;

    List<Action> actions;

    public void evaluateInput(Input input) {
        for (Action a : actions) {
            a.excecuteAction(input);
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
}
