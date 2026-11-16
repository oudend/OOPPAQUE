package org.example;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import ui.controller.Input;
import ui.controller.InputManager;
import ui.model.*;
import ui.view.LayoutComposer;

class AChangeColor extends MouseAction {
  String color;

  public AChangeColor(String color) {
    this.color = color;
  }

  @Override
  protected void onMouseLeftDown() {
    System.out.println(color);
  }
}

class AEmulateDraw implements Action {
  public void executeAction(Input input) {
    if (input.mouseLeft().hold()) {
      System.out.println("x: " + input.xPos() + " y: " + input.yPos());
    }
  }
}

public class Main {
  public static void main(String args[]) {
    InitWindow(800, 800, "Demo");
    SetTargetFPS(600);

    InputManager inputManager = new InputManager();

    BoxBlock root =
        new BoxBlock(
            new BoxProperties()
                .setBorderSize(5)
                .setBorderColor(BLACK)
                .setColor(WHITE)
                .setDirection(Direction.row)
                .setMarginY(0)
                .setMarginX(0)
                .setPaddingX(0)
                .setPaddingY(0),
            800,
            800,
            0,
            0);

    BoxBlock sidePanel =
        new BoxBlock(
            new BoxProperties()
                .setBorderSize(2)
                .setBorderColor(BLACK)
                .setColor(WHITE)
                .setDirection(Direction.column)
                .setMarginY(0)
                .setMarginX(0)
                .setPaddingX(20)
                .setPaddingY(20),
            150,
            800,
            5,
            5);

    BoxBlock RedColorBlock =
        new BoxBlock(
            new BoxProperties()
                .setBorderSize(2)
                .setBorderColor(BLACK)
                .setColor(RED)
                .setDirection(Direction.row)
                .setMarginY(50)
                .setMarginX(0)
                .setPaddingX(5)
                .setPaddingY(5),
            50,
            150,
            22,
            22);

    RedColorBlock.addAction(new AChangeColor("RED"));

    sidePanel.addChild(RedColorBlock);

    BoxBlock BlueColorBlock =
        new BoxBlock(
            new BoxProperties()
                .setBorderSize(2)
                .setBorderColor(BLACK)
                .setColor(BLUE)
                .setDirection(Direction.row)
                .setMarginY(50)
                .setMarginX(0)
                .setPaddingX(5)
                .setPaddingY(5),
            50,
            150,
            22,
            150);

    BlueColorBlock.addAction(new AChangeColor("BLUE"));

    sidePanel.addChild(BlueColorBlock);

    BoxBlock GreenColorBlock =
        new BoxBlock(
            new BoxProperties()
                .setBorderSize(2)
                .setBorderColor(BLACK)
                .setColor(GREEN)
                .setDirection(Direction.row)
                .setMarginY(50)
                .setMarginX(0)
                .setPaddingX(5)
                .setPaddingY(5),
            50,
            150,
            22,
            300);

    GreenColorBlock.addAction(new AChangeColor("GREEN"));

    sidePanel.addChild(GreenColorBlock);

    root.addChild(sidePanel);

    BoxBlock mainPanel =
        new BoxBlock(
            new BoxProperties()
                .setBorderSize(2)
                .setBorderColor(BLACK)
                .setColor(WHITE)
                .setDirection(Direction.row)
                .setMarginY(0)
                .setMarginX(0)
                .setPaddingX(0)
                .setPaddingY(0),
            650,
            800,
            152,
            2);

    mainPanel.addAction(new AEmulateDraw());

    root.addChild(mainPanel);

    LayoutLayer baseLayer = new LayoutLayer(root, 0, 0, 800, 800);

    LayoutComposer layoutComposer = new LayoutComposer();
    layoutComposer.addLayer(baseLayer);

    while (!WindowShouldClose()) {
      Input input = inputManager.getInput();
      root.evaluateInput(input);
      layoutComposer.draw();
    }
    CloseWindow();
  }
}
