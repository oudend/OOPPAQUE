package se.chalmers.group7.core.temp;

import se.chalmers.group7.core.canvas.BlendingMode;
import se.chalmers.group7.core.canvas.RaylibCanvas;

public class PreviewManager {
  private RaylibCanvas previewLayer;
  private BlendingMode previewBlending = BlendingMode.NORMAL;

  public PreviewManager(RaylibCanvas previewLayer, BlendingMode previewBlending) {
    this.previewLayer = previewLayer;
    this.previewBlending = previewBlending;
  }

  public RaylibCanvas getPreviewLayer() {
    return previewLayer;
  }

  public BlendingMode getPreviewBlending() {
    return previewBlending;
  }

  public void setPreviewBlending(BlendingMode previewBlending) {
    this.previewBlending = previewBlending;
  }
}
