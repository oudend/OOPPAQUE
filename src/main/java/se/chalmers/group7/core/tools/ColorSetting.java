package se.chalmers.group7.core.tools;

import com.raylib.Raylib.Color;

/** ColorSetting */
public class ColorSetting {
  Color value;

  public ColorSetting(Color initialColor) {
    this.value = initialColor;
  }

  public void setColor(Color drawColor) {
    this.value = drawColor;
  }

  public Color getValue() {
    return this.value;
  }
}
