package io.github.palexdev.devutils.settings;

import java.util.prefs.Preferences;

public class ChangelogGeneratorSettings extends Settings {
    //================================================================================
    // Singleton
    //================================================================================
    private static ChangelogGeneratorSettings settings;

    public static ChangelogGeneratorSettings get() {
        if (settings == null) settings = new ChangelogGeneratorSettings();
        return settings;
    }

    //================================================================================
    // Constructors
    //================================================================================
    private ChangelogGeneratorSettings() {}

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected Preferences initPrefs() {
        return Preferences.userRoot().node(Settings.getRootNode()).node("changelog");
    }
}
