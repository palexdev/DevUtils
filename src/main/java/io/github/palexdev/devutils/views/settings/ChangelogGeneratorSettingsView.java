package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.SettingsPane;
import io.github.palexdev.devutils.settings.descriptors.DescriptorType;
import io.github.palexdev.devutils.settings.descriptors.SettingDescriptor;
import io.github.palexdev.devutils.views.ChangelogGeneratorView;
import io.github.palexdev.devutils.views.MainView;

import static io.github.palexdev.devutils.settings.SettingsDB.CHANGELOG_DATE_FORMATTER;

public class ChangelogGeneratorSettingsView extends SettingsView {
    //================================================================================
    // Singleton
    //================================================================================
    private static final ChangelogGeneratorSettingsView instance = new ChangelogGeneratorSettingsView();

    public static ChangelogGeneratorSettingsView get() {
        return instance;
    }

    //================================================================================
    // Constructors
    //================================================================================
    private ChangelogGeneratorSettingsView() {
        build();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected void build() {
        onClose = () -> MainView.get().setView(ChangelogGeneratorView.get());
        super.build();

        SettingsPane sp = new SettingsPane("Changelog Generator Settings")
            .addSettings(SettingDescriptor.ofNonEmpty(DescriptorType.STRING, "Date Formatter", CHANGELOG_DATE_FORMATTER));
        root.setContent(sp);
        root.getStylesheets().add(Resources.loadCss("ChangelogView.css"));
    }
}
