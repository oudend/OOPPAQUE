package se.chalmers.group7.core.tools.settings;

public interface SettingView<T> {
  SettingKey<T> getKey();

  T getValue();
}
