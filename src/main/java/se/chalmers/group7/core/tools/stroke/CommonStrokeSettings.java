package se.chalmers.group7.core.tools.stroke;

import com.raylib.Raylib;
import se.chalmers.group7.core.tools.settings.SettingKey;

public class CommonStrokeSettings {
  public static final SettingKey<Float> SIZE = new SettingKey<>("size", Float.class);
  public static final SettingKey<Raylib.Color> COLOR =
      new SettingKey<>("color", Raylib.Color.class);
  public static final SettingKey<Float> OPACITY = new SettingKey<>("opacity", Float.class);
}
