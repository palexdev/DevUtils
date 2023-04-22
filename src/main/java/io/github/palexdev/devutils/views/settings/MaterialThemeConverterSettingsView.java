package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.settings.SettingsPane;
import io.github.palexdev.devutils.settings.MaterialThemeConverterSettings;
import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import io.github.palexdev.devutils.views.MaterialThemeConverterView;
import io.github.palexdev.devutils.views.base.SettingsView;
import org.springframework.stereotype.Component;

@Component
public class MaterialThemeConverterSettingsView extends SettingsView {
    //================================================================================
    // Properties
    //================================================================================
    private final MaterialThemeConverterSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    public MaterialThemeConverterSettingsView(MaterialThemeConverterSettings settings) {
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void onClose() {
        SpringHelper.setView(MaterialThemeConverterView.class);
    }

    @Override
    protected IconPane build() {
        IconPane root = super.build();
        SettingsPane sp = new SettingsPane("Material Theme Converter Settings")
            .addSettings(SettingDescriptor.of(Boolean.class, "Ignore Useless Tokens", settings.ignoreUselessTokens))
            .addSettings(SettingDescriptor.of(Boolean.class, "Include Typefaces", settings.includeTypefaces));
        root.setContent(sp);
        root.getStylesheets().add(Resources.loadViewCss("MaterialThemeConverterView.css"));
        return root;
    }
}
