package se.chalmers.group7.core.ui.controller;

import static com.raylib.Raylib.*;

import java.util.HashSet;
import java.util.Set;

public class InputManager {
  Set<Integer> oldKeyCodes = new HashSet<>();

  public Input getInput() {
    Set<Integer> keyCodes = new HashSet<>();

    int key = GetKeyPressed();
    while (key != 0) {
      keyCodes.add(key);
      key = GetKeyPressed();
    }
    for (int k : oldKeyCodes) {
      if (IsKeyDown(k)) keyCodes.add(k);
    }
    oldKeyCodes = keyCodes;

    Vector2 mouseDelta = GetMouseDelta();

    return new Input(
        GetMouseX(),
        GetMouseY(),
        GetMouseX(),
        GetMouseY(),
        keyCodes,
        mouseDelta.x(),
        mouseDelta.y(),
        new Button(
            IsMouseButtonPressed(MOUSE_BUTTON_LEFT),
            IsMouseButtonDown(MOUSE_BUTTON_LEFT),
            IsMouseButtonReleased(MOUSE_BUTTON_LEFT)),
        new Button(
            IsMouseButtonPressed(MOUSE_BUTTON_RIGHT),
            IsMouseButtonDown(MOUSE_BUTTON_RIGHT),
            IsMouseButtonReleased(MOUSE_BUTTON_RIGHT)),
        new Button(
            IsMouseButtonPressed(MOUSE_BUTTON_MIDDLE),
            IsMouseButtonDown(MOUSE_BUTTON_MIDDLE),
            IsMouseButtonReleased(MOUSE_BUTTON_MIDDLE)));
  }
}
