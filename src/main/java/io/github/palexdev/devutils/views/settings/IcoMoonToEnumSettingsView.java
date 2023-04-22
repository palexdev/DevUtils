package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.settings.SettingsPane;
import io.github.palexdev.devutils.settings.IcoMoonToEnumSettings;
import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import io.github.palexdev.devutils.views.IcoMoonToEnumView;
import io.github.palexdev.devutils.views.base.SettingsView;
import org.springframework.stereotype.Component;

@Component
public class IcoMoonToEnumSettingsView extends SettingsView {
    //================================================================================
    // Properties
    //================================================================================
    private final IcoMoonToEnumSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    public IcoMoonToEnumSettingsView(IcoMoonToEnumSettings settings) {
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void onClose() {
        SpringHelper.setView(IcoMoonToEnumView.class);
    }

    @Override
    protected IconPane build() {
        IconPane root = super.build();
        SettingsPane sp = new SettingsPane("IcoMoon To Enum Settings")
            .addSettings(SettingDescriptor.of(String.class, "Icons Class Prefix", settings.prefix));
        root.setContent(sp);
        root.getStylesheets().add(Resources.loadViewCss("IcoMoonToEnumView.css"));
        return root;
    }
}
