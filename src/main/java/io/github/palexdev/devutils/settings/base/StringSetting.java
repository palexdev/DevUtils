package io.github.palexdev.devutils.settings.base;

public class StringSetting extends Setting<String> {

    //================================================================================
    // Constructors
    //================================================================================
    public StringSetting(String name, String defaultValue, Settings container) {
        super(name, defaultValue, container);
    }

    public static StringSetting of(String name, String defaultValue, Settings container) {
        return new StringSetting(name, defaultValue, container);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public String get() {
        return container.prefs().get(name, defaultValue);
    }

    @Override
    public void set(String val) {
        container.prefs().put(name, val);
    }
}
