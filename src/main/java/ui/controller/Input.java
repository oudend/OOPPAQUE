package ui.controller;

import java.util.List;

public record Input(
        int xPos,
        int yPos,
        List<Integer> keyCodes,
        float deltaX,
        float deltaY,
        mouseButton mouseLeft,
        mouseButton mouseRight,
        mouseButton mouseMiddle
) { }
