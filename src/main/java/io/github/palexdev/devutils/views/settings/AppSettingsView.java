package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.SettingsPane;
import io.github.palexdev.devutils.settings.descriptors.DescriptorType;
import io.github.palexdev.devutils.settings.descriptors.SettingDescriptor;
import io.github.palexdev.devutils.views.MainView;

import static io.github.palexdev.devutils.settings.SettingsDB.USERNAME;

public class AppSettingsView extends SettingsView {
    //================================================================================
    // Singleton
    //================================================================================
    private static final AppSettingsView instance = new AppSettingsView();

    public static AppSettingsView get() {
        return instance;
    }

    //================================================================================
    // Constructors
    //================================================================================
    private AppSettingsView() {
        build();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void build() {
        onClose = () -> MainView.get().dashboard();
        super.build();

        SettingsPane sp = new SettingsPane("App Settings")
            .addSettings(SettingDescriptor.of(DescriptorType.STRING, "Username", USERNAME));
        root.setContent(sp);
        root.getStylesheets().add(Resources.loadCss("MainView.css"));
    }
}
