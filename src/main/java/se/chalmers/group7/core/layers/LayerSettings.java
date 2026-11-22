package se.chalmers.group7.core.layers;

import se.chalmers.group7.core.canvas.BlendingMode;

public class LayerSettings {
  private BlendingMode blendingMode = BlendingMode.NORMAL;
  private float opacity = 1.0f;
  private boolean visible = true;

  public LayerSettings() {}

  public BlendingMode getBlendingMode() {
    return blendingMode;
  }

  public void setBlendingMode(BlendingMode blendingMode) {
    this.blendingMode = blendingMode;
  }

  public float getOpacity() {
    return opacity;
  }

  public void setOpacity(float opacity) {
    this.opacity = Math.clamp(opacity, 0.0f, 1.0f);
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
