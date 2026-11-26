package se.chalmers.group7.core.tools.settings;

public class BoundedFloatSetting extends GenericSetting<Float> {
  private final float min;
  private final float max;

  public BoundedFloatSetting(SettingKey<Float> key, float value, float min, float max) {
    super(key, value);
    this.min = min;
    this.max = max;
    setValue(value);
  }

  @Override
  public void setValue(Float value) {
    super.setValue(Math.clamp(value, min, max));
  }
}
