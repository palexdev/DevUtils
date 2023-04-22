package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.settings.SettingsPane;
import io.github.palexdev.devutils.settings.AppSettings;
import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import io.github.palexdev.devutils.views.DashboardView;
import io.github.palexdev.devutils.views.base.SettingsView;
import org.springframework.stereotype.Component;

@Component
public class AppSettingsView extends SettingsView {
    //================================================================================
    // Properties
    //================================================================================
    private final AppSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    public AppSettingsView(AppSettings settings) {
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void onClose() {
        SpringHelper.setView(DashboardView.class);
    }

    @Override
    protected IconPane build() {
        IconPane root = super.build();
        SettingsPane sp = new SettingsPane("App Settings")
            .addSettings(SettingDescriptor.ofNonEmpty(String.class, "Username", settings.username));
        root.setContent(sp);
        root.getStylesheets().add(Resources.loadViewCss("MainView.css"));
        return root;
    }
}
