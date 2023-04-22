package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.settings.SettingsPane;
import io.github.palexdev.devutils.settings.ChangelogGeneratorSettings;
import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import io.github.palexdev.devutils.views.ChangelogGeneratorView;
import io.github.palexdev.devutils.views.base.SettingsView;
import org.springframework.stereotype.Component;

@Component
public class ChangelogGeneratorSettingsView extends SettingsView {
    //================================================================================
    // Properties
    //================================================================================
    private final ChangelogGeneratorSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    public ChangelogGeneratorSettingsView(ChangelogGeneratorSettings settings) {
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void onClose() {
        SpringHelper.setView(ChangelogGeneratorView.class);
    }

    @Override
    protected IconPane build() {
        IconPane root = super.build();
        SettingsPane sp = new SettingsPane("Changelog Generator Setting")
            .addSettings(SettingDescriptor.ofNonEmpty(String.class, "Date Format", settings.dateFormat));
        root.setContent(sp);
        root.getStylesheets().add(Resources.loadViewCss("ChangelogGeneratorView.css"));
        return root;
    }
}
