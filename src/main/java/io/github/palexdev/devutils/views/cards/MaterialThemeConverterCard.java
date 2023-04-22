package io.github.palexdev.devutils.views.cards;

import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.ServiceCard;
import io.github.palexdev.devutils.services.MaterialThemeConverterService;
import io.github.palexdev.devutils.views.MaterialThemeConverterView;
import io.github.palexdev.devutils.views.settings.MaterialThemeConverterSettingsView;

public class MaterialThemeConverterCard extends ServiceCard {

    public MaterialThemeConverterCard() {
        setText("Material Theme Converter");
        setIcon("fas-palette");
        setAction(() -> SpringHelper.setView(MaterialThemeConverterView.class));
        setSettingsAction(() -> SpringHelper.setView(MaterialThemeConverterSettingsView.class));
        setDescription(MaterialThemeConverterService.DESCRIPTION);
    }
}
