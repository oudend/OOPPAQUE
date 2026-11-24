package se.chalmers.group7.core.elements.CanvasBlock;

import static com.raylib.Raylib.*;

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
    if (!IsKeyDown(KEY_K)) canvas.prepareComposite();

    canvas.onInput(input);
  }
}
