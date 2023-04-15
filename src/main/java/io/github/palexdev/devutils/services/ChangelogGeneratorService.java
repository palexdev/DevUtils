package io.github.palexdev.devutils.services;

import io.github.palexdev.devutils.components.ServiceCard;
import io.github.palexdev.devutils.model.ChangelogGeneratorModel;
import io.github.palexdev.devutils.views.ChangelogGeneratorView;
import io.github.palexdev.devutils.views.MainView;
import io.github.palexdev.devutils.views.View;
import io.github.palexdev.devutils.views.settings.ChangelogGeneratorSettingsView;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;

public class ChangelogGeneratorService extends Service<ChangelogGeneratorModel> {
    //================================================================================
    // Singleton
    //================================================================================
    private static ChangelogGeneratorService instance;

    public static ChangelogGeneratorService get() {
        if (instance == null) instance = new ChangelogGeneratorService();
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private final ChangelogGeneratorModel model;

    //================================================================================
    // Constructors
    //================================================================================
    public ChangelogGeneratorService() {
        this.model = new ChangelogGeneratorModel();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public View view() {
        return ChangelogGeneratorView.get();
    }

    @Override
    public ServiceCard toCard() {
        if (card == null) {
            card = new ServiceCard(
                "Changelog Generator",
                new MFXFontIcon("fas-file-lines")
            );
            card.setAction(() -> MainView.get().setView(ChangelogGeneratorView.get()));
            card.setSettingsAction(() -> MainView.get().setView(ChangelogGeneratorSettingsView.get()));
            card.setDescription(description());
        }
        return card;
    }

    @Override
    public ChangelogGeneratorModel model() {
        return model;
    }

    @Override
    public String description() {
        return ServicesDescriptions.CHANGELOG_GENERATOR_SERVICE;
    }
}
