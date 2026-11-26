package se.chalmers.group7.core.tools;

public class NumericSetting {
  private final NumericSettingType type;
  private float value;

  public NumericSetting(NumericSettingType type) {
    this.type = type;
    setValue(type.getDefaultValue());
  }

  public float getValue() {
    return this.value;
  }

  public void increase(float amount) {
    setValue(this.value + amount);
  }

  public void decrease(float amount) {
    setValue(this.value - amount);
  }

  private void setValue(float value) {
    this.value = Math.max(type.getMin(), Math.min(value, type.getMax()));
  }
}
