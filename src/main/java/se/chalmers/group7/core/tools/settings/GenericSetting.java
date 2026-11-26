package se.chalmers.group7.core.tools.settings;

public class GenericSetting<T> implements Setting<T>, SettingView<T> {
  protected final SettingKey<T> key;
  protected T value;

  public GenericSetting(SettingKey<T> key, T value) {
    this.key = key;
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public SettingView<T> getView() {
    return this;
  }

  @Override
  public SettingKey<T> getKey() {
    return key;
  }

  @Override
  public void setValue(T value) {
    this.value = value;
  }
}
