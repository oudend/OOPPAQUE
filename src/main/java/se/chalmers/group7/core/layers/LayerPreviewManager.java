package se.chalmers.group7.core.layers;

import com.raylib.Raylib;
import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.Canvas;

public class LayerPreviewManager {
  private Canvas previewCanvas;
  private BlendingMode previewBlending;

  public LayerPreviewManager(Canvas previewCanvas, BlendingMode previewBlending) {
    this.previewCanvas = previewCanvas;
    this.previewBlending = previewBlending;
  }

  public Canvas getPreviewCanvas() {
    return previewCanvas;
  }

  public BlendingMode getPreviewBlending() {
    return previewBlending;
  }

  public void setPreviewCanvas(Canvas previewLayer) {
    this.previewCanvas = previewLayer;
  }

  public void setPreviewBlending(BlendingMode previewBlending) {
    this.previewBlending = previewBlending;
  }

  public void clear() {
    this.previewCanvas.clear();
    previewBlending = BlendingMode.NORMAL;
  }

  public Raylib.Texture getTexture() {
    return this.previewCanvas.getTexture();
  }
}
