package io.github.palexdev.devutils.settings.descriptors;

import io.github.palexdev.devutils.settings.SettingsDB;

public record SettingDescriptor(DescriptorType type, String description, SettingsDB setting) {

    public static SettingDescriptor of(DescriptorType type, String description, SettingsDB setting) {
        return new SettingDescriptor(type, description, setting);
    }
}
