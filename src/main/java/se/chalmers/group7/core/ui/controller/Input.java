package se.chalmers.group7.core.ui.controller;

import java.util.Set;

public record Input(
    int xPos,
    int yPos,
    int globalX,
    int globalY,
    Set<Integer> keyCodes,
    float deltaX,
    float deltaY,
    Button mouseLeft,
    Button mouseRight,
    Button mouseMiddle) {
  public Input enterArea(int x, int y) {
    return new Input(
        this.globalX - x,
        this.globalY - y,
        globalX,
        globalY,
        keyCodes,
        deltaX,
        deltaY,
        mouseLeft,
        mouseRight,
        mouseMiddle);
  }
}
