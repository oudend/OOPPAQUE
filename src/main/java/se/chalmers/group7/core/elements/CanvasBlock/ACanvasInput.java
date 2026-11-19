package se.chalmers.group7.core.elements.CanvasBlock;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.KEY_LEFT_CONTROL;
import static com.raylib.Raylib.KEY_W;
import static com.raylib.Raylib.KEY_Y;
import static com.raylib.Raylib.KEY_Z;

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
      if (input.keyCodes().contains(KEY_M)) {
        rlCheckErrors();
      }

      if (input.keyCodes().contains(KEY_M)) {
        System.out.println(IsTextureValid(canvas.getTransparencyPattern().texture()));

        rlCheckErrors();
      }
    }

    if (!input.keyCodes().contains(KEY_K)) canvas.prepareComposite();

    if (input.keyCodes().contains(KEY_W)) {
      canvas.setSelectedTool(1);
    } else {
      canvas.setSelectedTool(0);
    }

    if (input.mouseLeft().down()) {
      canvas.getSelectedTool().onPointerDown(input.xPos(), input.yPos());
    }

    if (input.mouseLeft().released()) {
      canvas.getSelectedTool().onPointerUp();
    }

    if (input.deltaX() > 1 || input.deltaY() > 1) {
      canvas.getSelectedTool().onPointerMove(input.xPos(), input.yPos());
    }

    if (input.keyCodes().contains(KEY_LEFT_CONTROL) && input.keyCodes().contains(KEY_Z)) {
      canvas.getOperationManager().undo();
    }

    if (input.keyCodes().contains(KEY_LEFT_CONTROL) && input.keyCodes().contains(KEY_Y)) {
      canvas.getOperationManager().redo();
    }
  }
}
