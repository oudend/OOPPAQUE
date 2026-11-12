package ui.controller;

import java.util.ArrayList;
import java.util.List;

import static com.raylib.Raylib.*;

public class InputManager {
    public Input getInput() {
        List<Integer> keyCodes = new ArrayList<>();

        int key = GetKeyPressed();
        while (key != 0) {
            keyCodes.add(key);
            key = GetKeyPressed();
        }

        Vector2 mouseDelta = GetMouseDelta();

        return new Input(
                GetMouseX(),
                GetMouseY(),
                keyCodes,
                mouseDelta.x(),
                mouseDelta.y(),
                new mouseButton(
                        IsMouseButtonDown(MOUSE_BUTTON_LEFT),
                        IsMouseButtonPressed(MOUSE_BUTTON_LEFT),
                        IsMouseButtonReleased(MOUSE_BUTTON_LEFT)
                ),
                new mouseButton(
                        IsMouseButtonDown(MOUSE_BUTTON_RIGHT),
                        IsMouseButtonPressed(MOUSE_BUTTON_RIGHT),
                        IsMouseButtonReleased(MOUSE_BUTTON_RIGHT)
                ),
                new mouseButton(
                        IsMouseButtonDown(MOUSE_BUTTON_MIDDLE),
                        IsMouseButtonPressed(MOUSE_BUTTON_MIDDLE),
                        IsMouseButtonReleased(MOUSE_BUTTON_MIDDLE)
                )
        );
    }
}
