package se.chalmers.group7.core;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import com.raylib.Raylib.Color;
import se.chalmers.group7.core.elements.BarSlider.ASliderInput;
import se.chalmers.group7.core.elements.BarSlider.BarSlider;
import se.chalmers.group7.core.elements.CanvasBlock.ACanvasInput;
import se.chalmers.group7.core.elements.CanvasBlock.AChangeColor;
import se.chalmers.group7.core.elements.CanvasBlock.CanvasBlock;
import se.chalmers.group7.core.ui.controller.Input;
import se.chalmers.group7.core.ui.controller.InputManager;
import se.chalmers.group7.core.ui.model.*;
import se.chalmers.group7.core.ui.view.LayoutComposer;

public class App {
  private final boolean debug;

  private final int width;
  private final int height;

  private final InputManager inputManager = new InputManager();
  private LayoutComposer layoutComposer;
  private LayoutBlock root;

  public App(int width, int height, boolean debug) {
    this.debug = debug;

    this.width = width;
    this.height = height;

    InitWindow(this.width, this.height, "Demo");
    SetTargetFPS(60);

    initializeLayout();
  }

  private void initializeLayout() {
    BoxBlock rootBox = new BoxBlock(
        new BoxProperties().setColor(DARKGRAY).setDirection(Direction.column), width, height);

    BoxBlock workAreaBox = new BoxBlock(
        new BoxProperties().setColor(DARKGRAY).setDirection(Direction.row), width, height - 80);

    BoxBlock canvasBox = new BoxBlock(
        new BoxProperties().setBorderSize(5).setBorderColor(GRAY).setMarginY(15).setMarginX(15),
        width / 2,
        height);

    BoxBlock sideBox = new BoxBlock(
        new BoxProperties().setBorderSize(5).setBorderColor(GRAY).setMarginY(15).setMarginX(15),
        width / 2,
        height);

    CanvasBlock canvas = new CanvasBlock(width - 40, height - 99);
    canvas.addAction(new ACanvasInput(canvas, debug));
    canvasBox.addChild(canvas);

    BoxBlock colorPalette = new BoxBlock(
        new BoxProperties().setColor(LIGHTGRAY).setBorderColor(BLACK).setPaddingY(12),
        width,
        59);

    Color[] colors = {
        BLACK, WHITE, RED, GREEN, BLUE, YELLOW, DARKBLUE, DARKGREEN, PURPLE, PINK, ORANGE, BROWN,
        DARKBROWN, RAYWHITE, LIGHTGRAY, GRAY, DARKGRAY
    };
    // trust the process
    int colorBlockSize = 35;
    int colorSpacing = (width - (colorBlockSize * colors.length)) / (2 * (colors.length - 1));
    for (Color color : colors) {
      LayoutBlock colorBlock = new BoxBlock(
          new BoxProperties()
              .setColor(color)
              .setBorderColor(BLACK)
              .setBorderSize(5)
              .setMarginX(colorSpacing),
          colorBlockSize + 2 * colorSpacing,
          colorBlockSize);

      colorBlock.addAction(new AChangeColor(canvas, color));

      colorPalette.addChild(colorBlock);
    }

    // Add slider to sidebar
    BarSlider slider = new BarSlider(200, 50, 50, 5, 0, 1, 0);

    slider.addAction(new ASliderInput(slider));

    sideBox.addChild(slider);

    workAreaBox.addChild(canvasBox);
    workAreaBox.addChild(sideBox);

    rootBox.addChild(colorPalette);
    rootBox.addChild(workAreaBox);

    root = rootBox;
    if (debug) {
      root.addAction(
          new Action() {
            @Override
            public void executeAction(Input input) {
              if (IsKeyDown(KEY_M)) {
                rlCheckErrors();
              }
            }
          });
    }
    LayoutLayer layer = new LayoutLayer(root, 0, 0, width, height);

    layoutComposer = new LayoutComposer();
    layoutComposer.addLayer(layer);
  }

  public void run() {
    while (!WindowShouldClose()) {
      Input input = inputManager.getInput();
      root.evaluateInput(input);

      if (debug) {
        layoutComposer.drawWithFPS();
      } else {
        layoutComposer.draw();
      }
    }
    CloseWindow();
  }
}
