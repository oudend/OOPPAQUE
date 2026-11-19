package se.chalmers.group7.core;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import se.chalmers.group7.core.elements.CanvasBlock.ACanvasInput;
import se.chalmers.group7.core.elements.CanvasBlock.CanvasBlock;
import se.chalmers.group7.core.ui.controller.Input;
import se.chalmers.group7.core.ui.controller.InputManager;
import se.chalmers.group7.core.ui.model.BoxBlock;
import se.chalmers.group7.core.ui.model.BoxProperties;
import se.chalmers.group7.core.ui.model.LayoutBlock;
import se.chalmers.group7.core.ui.model.LayoutLayer;
import se.chalmers.group7.core.ui.view.LayoutComposer;

public class App {
  private final boolean debug;

  private final int width;
  private final int height;

  private final InputManager inputManager = new InputManager();
  private final LayoutComposer layoutComposer;
  private final LayoutBlock root;

  public App(int width, int height, boolean debug) {
    this.debug = debug;

    this.width = width;
    this.height = height;

    InitWindow(this.width, this.height, "Demo");
    SetTargetFPS(6000);

    BoxBlock rootBox = new BoxBlock(new BoxProperties().setColor(DARKGRAY), width, height, 0, 0);

    BoxBlock canvasBox =
        new BoxBlock(
            new BoxProperties().setBorderSize(5).setBorderColor(GRAY).setMarginY(15).setMarginX(15),
            width,
            height,
            0,
            0);

    rootBox.addChild(canvasBox);

    CanvasBlock canvas = new CanvasBlock(width - 40, height - 40, 20, 20);
    canvas.addAction(new ACanvasInput(canvas, debug));
    canvasBox.addChild(canvas);
    root = rootBox;
    LayoutLayer layer = new LayoutLayer(root, 0, 0, width, height);
    layoutComposer = new LayoutComposer();
    layoutComposer.addLayer(layer);
  }

  public void run() {
    while (!WindowShouldClose()) {
      Input input = inputManager.getInput();
      root.evaluateInput(input);
      layoutComposer.draw();

      if (debug) {
        layoutComposer.drawWithFPS();
      } else {
        layoutComposer.draw();
      }
    }
    CloseWindow();
  }
}
