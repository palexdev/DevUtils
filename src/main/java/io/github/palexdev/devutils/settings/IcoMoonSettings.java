package io.github.palexdev.devutils.settings;

import java.util.prefs.Preferences;

public class IcoMoonSettings extends Settings {
	//================================================================================
	// Singleton
	//================================================================================
	private static IcoMoonSettings settings;

	public static IcoMoonSettings get() {
		if (settings == null) settings = new IcoMoonSettings();
		return settings;
	}

	//================================================================================
	// Constructors
	//================================================================================
	private IcoMoonSettings() {}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Preferences initPrefs() {
		return Preferences.userRoot().node(Settings.getRootNode()).node("icomoon");
	}
}
