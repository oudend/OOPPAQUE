package se.chalmers.group7.core.tools.settings;

public interface Setting<T> {
  SettingView<T> getView();

  SettingKey<T> getKey();

  void setValue(T value);
}
