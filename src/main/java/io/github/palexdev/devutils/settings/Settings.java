package io.github.palexdev.devutils.settings;

import java.util.prefs.Preferences;

public abstract class Settings {
	//================================================================================
	// Properties
	//================================================================================
	protected final Preferences prefs;

	//================================================================================
	// Constructors
	//================================================================================
	protected Settings() {
		prefs = initPrefs();
	}

	//================================================================================
	// Abstract Methods
	//================================================================================
	protected abstract Preferences initPrefs();

	//================================================================================
	// Delegate Methods
	//================================================================================
	protected void put(String key, String value) {
		prefs.put(key, value);
	}

	protected String get(String key, String def) {
		return prefs.get(key, def);
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static String getRootNode() {
		return "io/github/palexdev/devutils";
	}
}
