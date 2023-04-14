package io.github.palexdev.devutils.settings;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.function.Supplier;

public enum SettingsDB {
    USERNAME("user.name", "", AppSettings::get),
    WINDOW_SIZES("window.sizes", "1024,780", AppSettings::get),

    ICOMOON_LAST_OPEN_DIR("icomoon.lastopnedir", System.getProperty("user.home"), IcoMoonSettings::get),
    ICOMOON_LAST_SAVE_DIR("icomoon.lastsavedir", System.getProperty("user.home"), IcoMoonSettings::get),
    ICOMOON_PREFIX("icomoon.prefix", "", IcoMoonSettings::get),
    ;

    //================================================================================
    // Methods
    //================================================================================
    public void set(Object value) {
        Settings settings = settings();
        String toString = String.valueOf(value);
        settings.put(key(), toString);
        setValue(toString);
    }

    public String get() {
        return get("");
    }

    public String get(String defVal) {
        Settings settings = settings();
        String val = settings.get(key(), defVal);
        setValue(val);
        return val;
    }

    public ReadOnlyStringProperty valueProperty() {
        return value.getReadOnlyProperty();
    }

    public void reset() {
        set(defaultVal());
    }

    public static void resetAll() {
        for (SettingsDB setting : values()) {
            setting.reset();
        }
    }

    private void setValue(String value) {
        this.value.setValue(value);
    }

    //================================================================================
    // Misc
    //================================================================================
    private final String key;
    private final String defaultVal;
    private final Supplier<Settings> settings;
    private final ReadOnlyStringWrapper value = new ReadOnlyStringWrapper();

    SettingsDB(String key, String defaultVal, Supplier<Settings> settings) {
        this.key = key;
        this.defaultVal = defaultVal;
        this.settings = settings;
    }

    public String key() {
        return key;
    }

    public String defaultVal() {
        return defaultVal;
    }

    public Settings settings() {
        return settings.get();
    }
}
