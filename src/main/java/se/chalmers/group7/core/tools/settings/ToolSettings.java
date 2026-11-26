package se.chalmers.group7.core.tools.settings;

import java.util.*;
import java.util.function.UnaryOperator;

public class ToolSettings {

  private final Map<SettingKey<?>, Setting<?>> settings = new HashMap<>();

  public ToolSettings(List<Setting<?>> settings) {
    for (Setting<?> setting : settings) {
      addSetting(setting);
    }
  }

  public <T> void addSetting(Setting<T> setting) {
    settings.put(setting.getKey(), setting);
  }

  List<SettingView<?>> getAllSettings() {
    List<SettingView<?>> result = new ArrayList<>();

    for (Setting<?> setting : settings.values()) {
      result.add(setting.getView());
    }

    return result;
  }

  private <T> Optional<Setting<T>> getSetting(SettingKey<T> key) {
    Setting<?> setting = settings.get(key);

    if (setting == null) return Optional.empty();

    @SuppressWarnings("unchecked")
    Setting<T> typedSetting = (Setting<T>) setting;

    return Optional.of(typedSetting);
  }

  public <T> T getSettingValue(SettingKey<T> key) {
    return getSettingView(key).getValue();
  }

  public <T> SettingKey<T> getSettingKey(SettingKey<T> key) {
    return getSettingView(key).getKey();
  }

  public <T> SettingView<T> getSettingView(SettingKey<T> key) {
    Setting<T> setting = getSetting(key).orElseThrow();

    return setting.getView();
  }

  public <T> void updateSetting(SettingKey<T> key, UnaryOperator<T> updater) {
    Setting<T> setting = getSetting(key).orElseThrow();

    setting.setValue(updater.apply(setting.getView().getValue()));
  }

  public <T> void updateSettingIfExists(SettingKey<T> key, UnaryOperator<T> updater) {
    Optional<Setting<T>> optionalSetting = getSetting(key);

    if (optionalSetting.isEmpty()) return;

    Setting<T> setting = optionalSetting.get();

    setting.setValue(updater.apply(setting.getView().getValue()));
  }
}
