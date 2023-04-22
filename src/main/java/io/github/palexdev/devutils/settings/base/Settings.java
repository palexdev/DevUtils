package io.github.palexdev.devutils.settings.base;

import io.github.palexdev.devutils.events.ResetSettingsEvent;
import org.springframework.context.event.EventListener;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public abstract class Settings {
    //================================================================================
    // Properties
    //================================================================================
    protected final Preferences prefs;
    protected final Set<Setting<?>> settings;

    //================================================================================
    // Constructors
    //================================================================================
    protected Settings() {
        prefs = init();
        settings = new LinkedHashSet<>();
    }

    //================================================================================
    // Abstract Methods
    //================================================================================
    protected abstract String node();

    //================================================================================
    // Methods
    //================================================================================
    protected Preferences init() {
        return Preferences.userRoot().node(node());
    }

    protected StringSetting registerString(String name, String defaultValue) {
        StringSetting setting = StringSetting.of(name, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    protected BooleanSetting registerBoolean(String name, boolean defaultValue) {
        BooleanSetting setting = BooleanSetting.of(name, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    protected NumberSetting<Double> registerDouble(String name, double defaultValue) {
        NumberSetting<Double> setting = NumberSetting.forDouble(name, defaultValue, this);
        settings.add(setting);
        return setting;
    }

    public void reset() {
        settings.forEach(Setting::reset);
    }

    public void onChange(PreferenceChangeListener pcl) {
        prefs.addPreferenceChangeListener(pcl);
    }

    public void removeOnChange(PreferenceChangeListener pcl) {
        prefs.removePreferenceChangeListener(pcl);
    }

    //================================================================================
    // Event Handling
    //================================================================================
    @EventListener
    public void onResetRequest(ResetSettingsEvent event) {
        Class<? extends Settings> c = event.getSettingsClass();
        if (c == Settings.class || c == getClass()) reset();
    }

    //================================================================================
    // Static Methods
    //================================================================================
    public static String root() {
        return "/io/github/palexdev/devutils";
    }

    //================================================================================
    // Getters
    //================================================================================
    protected Preferences prefs() {
        return prefs;
    }

    public Set<Setting<?>> getSettings() {
        return Collections.unmodifiableSet(settings);
    }
}
