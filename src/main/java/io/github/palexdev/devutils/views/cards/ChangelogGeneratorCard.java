package io.github.palexdev.devutils.views.cards;

import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.ServiceCard;
import io.github.palexdev.devutils.services.ChangelogGeneratorService;
import io.github.palexdev.devutils.views.ChangelogGeneratorView;
import io.github.palexdev.devutils.views.settings.ChangelogGeneratorSettingsView;

public class ChangelogGeneratorCard extends ServiceCard {

    public ChangelogGeneratorCard() {
        setText("Changelog Generator");
        setIcon("fas-file-lines");
        setAction(() -> SpringHelper.setView(ChangelogGeneratorView.class));
        setSettingsAction(() -> SpringHelper.setView(ChangelogGeneratorSettingsView.class));
        setDescription(ChangelogGeneratorService.DESCRIPTION);
    }
}
