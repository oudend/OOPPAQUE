package ui.controller;

public record Input(
        int xPos,
        int yPos,
        int[] keyCodes,
        int deltaX,
        int deltaY,
        mouseButton mouseLeft,
        mouseButton mouseRight,
        mouseButton mouseMiddle
) { }
