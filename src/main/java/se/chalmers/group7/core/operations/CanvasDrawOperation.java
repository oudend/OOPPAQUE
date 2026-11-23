package se.chalmers.group7.core.operations;

import static se.chalmers.group7.core.operations.utils.TextureDiffUtil.*;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;

public class CanvasDrawOperation extends Operation {
  private Canvas canvas;

  private BlendingMode blendingMode;
  private final byte[] diff;

  public CanvasDrawOperation(Canvas canvas, BlendingMode blendingMode, Raylib.Texture texture) {
    super();

    this.canvas = canvas;
    this.blendingMode = blendingMode;

    byte[] before = readPixels(this.canvas.getTexture());

    Canvas canvasClone = this.canvas.clone();

    canvasClone.drawTexture(texture, 0, 0, canvas.getWidth(), canvas.getHeight(), blendingMode);

    byte[] after = readPixels(canvasClone.getTexture());

    this.diff = computeDiff(before, after);

    canvasClone.dispose();

    redo();
  }

  @Override
  public void redo() {
    super.redo();

    applyDiff(canvas.getTexture(), diff);
  }

  @Override
  public void undo() {
    super.undo();

    applyDiff(canvas.getTexture(), diff);
  }
}
