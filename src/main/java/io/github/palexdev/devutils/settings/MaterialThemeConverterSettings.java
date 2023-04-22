package io.github.palexdev.devutils.settings;

import io.github.palexdev.devutils.settings.base.BooleanSetting;
import io.github.palexdev.devutils.settings.base.Settings;
import io.github.palexdev.devutils.settings.base.StringSetting;
import org.springframework.stereotype.Component;

@Component
public class MaterialThemeConverterSettings extends Settings {
    //================================================================================
    // Settings
    //================================================================================
    public final BooleanSetting ignoreUselessTokens = registerBoolean("ignore.useless.tokens", true);
    public final BooleanSetting includeTypefaces = registerBoolean("include.typefaces", true);
    public final StringSetting lastOpenDir = registerString("last.open.dir", System.getProperty("user.home"));
    public final StringSetting lastSaveDir = registerString("last.save.dir", System.getProperty("user.home"));

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected String node() {
        return root() + "/material-theme-converter";
    }
}
