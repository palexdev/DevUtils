package io.github.palexdev.devutils.settings;

import io.github.palexdev.devutils.settings.base.Settings;
import io.github.palexdev.devutils.settings.base.StringSetting;
import org.springframework.stereotype.Component;

@Component
public class IcoMoonToEnumSettings extends Settings {
    //================================================================================
    // Settings
    //================================================================================
    public final StringSetting prefix = registerString("icon.class.prefix", "");
    public final StringSetting lastOpenDir = registerString("last.open.dir", System.getProperty("user.home"));
    public final StringSetting lastSaveDir = registerString("last.save.dir", System.getProperty("user.home"));

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected String node() {
        return root() + "/icomoon-to-enum";
    }
}
