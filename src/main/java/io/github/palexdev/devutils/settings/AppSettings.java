package io.github.palexdev.devutils.settings;

import java.util.prefs.Preferences;

public class AppSettings extends Settings {
	//================================================================================
	// Singleton
	//================================================================================
	private static AppSettings settings;

	public static AppSettings get() {
		if (settings == null) settings = new AppSettings();
		return settings;
	}

	//================================================================================
	// Constructors
	//================================================================================
	private AppSettings() {}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Preferences initPrefs() {
		return Preferences.userRoot().node(Settings.getRootNode()).node("app");
	}
}
