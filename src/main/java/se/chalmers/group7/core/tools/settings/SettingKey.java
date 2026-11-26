package se.chalmers.group7.core.tools.settings;

public record SettingKey<T>(String id, Class<T> type) {}
