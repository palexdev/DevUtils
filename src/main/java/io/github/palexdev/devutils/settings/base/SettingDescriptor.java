package io.github.palexdev.devutils.settings.base;

import io.github.palexdev.devutils.components.settings.BooleanSettingComponent;
import io.github.palexdev.devutils.components.settings.FieldSettingComponent;
import io.github.palexdev.devutils.components.settings.SettingComponent;

public record SettingDescriptor<T>(Class<T> type, String description, Setting<T> setting, boolean avoidEmpty) {

    //================================================================================
    // Methods
    //================================================================================
    /* Convenient, fluent, converters to components
     * Not the most appealing way but simple
     */
    @SuppressWarnings("unchecked")
    public SettingComponent<?> toComponent() {
        return switch (type) {
            case Class<?> k when k == Boolean.class -> new BooleanSettingComponent((SettingDescriptor<Boolean>) this);
            case Class<?> k when k == String.class -> FieldSettingComponent.string((SettingDescriptor<String>) this);
            case Class<?> k when k == Double.class -> FieldSettingComponent.forDouble((SettingDescriptor<Double>) this);
            default -> null;
        };
    }

    //================================================================================
    // Static Methods
    //================================================================================
    public static <T> SettingDescriptor<T> of(Class<T> type, String description, Setting<T> setting) {
        return new SettingDescriptor<>(type, description, setting, false);
    }

    public static <T> SettingDescriptor<T> ofNonEmpty(Class<T> type, String description, Setting<T> setting) {
        return new SettingDescriptor<>(type, description, setting, true);
    }

    //================================================================================
    // Delegate Methods
    //================================================================================
    public Settings settings() {
        return setting.container();
    }
}
