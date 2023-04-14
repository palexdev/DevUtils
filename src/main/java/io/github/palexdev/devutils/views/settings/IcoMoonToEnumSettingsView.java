package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.SettingsPane;
import io.github.palexdev.devutils.settings.descriptors.DescriptorType;
import io.github.palexdev.devutils.settings.descriptors.SettingDescriptor;
import io.github.palexdev.devutils.views.IcoMoonToEnumView;
import io.github.palexdev.devutils.views.MainView;

import static io.github.palexdev.devutils.settings.SettingsDB.ICOMOON_PREFIX;

public class IcoMoonToEnumSettingsView extends SettingsView {
    //================================================================================
    // Singleton
    //================================================================================
    private static final IcoMoonToEnumSettingsView instance = new IcoMoonToEnumSettingsView();

    public static IcoMoonToEnumSettingsView get() {
        return instance;
    }

    //================================================================================
    // Constructors
    //================================================================================
    private IcoMoonToEnumSettingsView() {
        build();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void build() {
        onClose = () -> MainView.get().setView(IcoMoonToEnumView.get());
        super.build();

        SettingsPane sp = new SettingsPane("IcoMoon To Enum Service Settings")
            .addSettings(SettingDescriptor.of(DescriptorType.STRING, "Class Prefix", ICOMOON_PREFIX));
        root.setContent(sp);
        root.getStylesheets().add(Resources.loadCss("IcoMoonView.css"));
    }
}
