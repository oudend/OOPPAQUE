package se.chalmers.group7.core.tools;

public enum NumericSettingType {
  SIZE(15f, 1f, 200f),
  OPACITY(0.5f, 0f, 1f);

  private final float defaultValue;
  private final float min;
  private final float max;

  NumericSettingType(float defaultValue, float min, float max) {
    this.defaultValue = defaultValue;
    this.min = min;
    this.max = max;
  }

  public float getDefaultValue() {
    return defaultValue;
  }

  public float getMin() {
    return min;
  }

  public float getMax() {
    return max;
  }
}
