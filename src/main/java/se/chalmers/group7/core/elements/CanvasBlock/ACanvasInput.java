package se.chalmers.group7.core.elements.CanvasBlock;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.KEY_LEFT_CONTROL;
import static com.raylib.Raylib.KEY_W;
import static com.raylib.Raylib.KEY_Y;
import static com.raylib.Raylib.KEY_Z;

import se.chalmers.group7.core.tools.Brush;
import se.chalmers.group7.core.tools.Eraser;
import se.chalmers.group7.core.ui.controller.Input;
import se.chalmers.group7.core.ui.model.Action;

public class ACanvasInput implements Action {
  private final CanvasBlock canvas;

  private final boolean debug;

  public ACanvasInput(CanvasBlock canvas) {
    this.canvas = canvas;
    debug = false;
  }

  public ACanvasInput(CanvasBlock canvas, boolean debug) {
    this.canvas = canvas;
    this.debug = debug;
  }

  public void executeAction(Input input) {
    if (debug) {
      if (IsKeyDown(KEY_M)) {
        System.out.println(IsTextureValid(canvas.getTransparencyPattern().texture()));

        rlCheckErrors();
      }
    }

    if (!IsKeyDown(KEY_K)) canvas.prepareComposite();

    if (IsKeyDown(KEY_W)) {
      canvas.setSelectedTool(Eraser.getInstance());
    } else {
      canvas.setSelectedTool(Brush.getInstance());
    }

    if (input.mouseLeft().down()) {
      canvas.getSelectedTool().onPointerDown(input.xPos(), input.yPos());
    }

    if (input.mouseLeft().released()) {
      canvas.getSelectedTool().onPointerUp();
    }

    if ((input.deltaX() * input.deltaX() + input.deltaY() * input.deltaY()) / GetFrameTime()  > 1) {
      canvas.getSelectedTool().onPointerMove(input.xPos(), input.yPos());
    }

    if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Z)) {
      canvas.getOperationManager().undo();
    }

    if (IsKeyDown(KEY_LEFT_CONTROL) && IsKeyPressed(KEY_Y)) {
      canvas.getOperationManager().redo();
    }
  }
}
