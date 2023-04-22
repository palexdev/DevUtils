package io.github.palexdev.devutils.settings;

import io.github.palexdev.devutils.settings.base.NumberSetting;
import io.github.palexdev.devutils.settings.base.Settings;
import io.github.palexdev.devutils.settings.base.StringSetting;
import org.springframework.stereotype.Component;

@Component
public class AppSettings extends Settings {
    //================================================================================
    // Settings
    //================================================================================
    public final StringSetting username = registerString("username", "User");
    public final NumberSetting<Double> windowWidth = registerDouble("window.width", 1024.0);
    public final NumberSetting<Double> windowHeight = registerDouble("window.height", 720.0);

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected String node() {
        return root();
    }

    //================================================================================
    // Additional Environment Settings
    //================================================================================
    public boolean isResetSettings() {
        try {
            String env = System.getenv("RESET_SETTINGS");
            return Boolean.parseBoolean(env);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isDebug() {
        try {
            String env = System.getenv("DEBUG");
            return Boolean.parseBoolean(env);
        } catch (Exception ex) {
            return false;
        }
    }
}
