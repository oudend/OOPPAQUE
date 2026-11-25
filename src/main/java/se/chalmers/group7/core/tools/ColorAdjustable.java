package se.chalmers.group7.core.tools;

import com.raylib.Raylib.Color;

/** ColorAdjustable */
public interface ColorAdjustable {

  ColorSetting GetColor();

  default void SetColor(Color newColor) {
    GetColor().setColor(newColor);
  }
}
