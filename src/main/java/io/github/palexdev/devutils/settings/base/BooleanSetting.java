package io.github.palexdev.devutils.settings.base;

public class BooleanSetting extends Setting<Boolean> {
    //================================================================================
    // Constructors
    //================================================================================
    public BooleanSetting(String name, boolean defaultValue, Settings container) {
        super(name, defaultValue, container);
    }

    public static BooleanSetting of(String name, boolean defaultValue, Settings container) {
        return new BooleanSetting(name, defaultValue, container);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Boolean get() {
        return container.prefs().getBoolean(name, defaultValue);
    }

    @Override
    public void set(Boolean val) {
        container.prefs().putBoolean(name, val);
    }
}
