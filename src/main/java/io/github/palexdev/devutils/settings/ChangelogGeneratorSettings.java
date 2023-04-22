package io.github.palexdev.devutils.settings;

import io.github.palexdev.devutils.settings.base.Settings;
import io.github.palexdev.devutils.settings.base.StringSetting;
import org.springframework.stereotype.Component;

@Component
public class ChangelogGeneratorSettings extends Settings {
    //================================================================================
    // Settings
    //================================================================================
    public final StringSetting dateFormat = registerString("date.format", "dd-MM-yyyy");

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected String node() {
        return root() + "/changelog-generator";
    }
}
