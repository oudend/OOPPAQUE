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
    mouseButton mouseLeft,
    mouseButton mouseRight,
    mouseButton mouseMiddle) {
  public Input enterArea(int x, int y) {
    return new Input(
        this.xPos - x,
        this.yPos - y,
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
