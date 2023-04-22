package io.github.palexdev.devutils.views.cards;

import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.ServiceCard;
import io.github.palexdev.devutils.services.IcoMoonToEnumService;
import io.github.palexdev.devutils.views.IcoMoonToEnumView;
import io.github.palexdev.devutils.views.settings.IcoMoonToEnumSettingsView;

public class IcoMoonToEnumCard extends ServiceCard {

    public IcoMoonToEnumCard() {
        setText("IcoMoon To Enum Converter");
        setIcon("fas-icons");
        setAction(() -> SpringHelper.setView(IcoMoonToEnumView.class));
        setSettingsAction(() -> SpringHelper.setView(IcoMoonToEnumSettingsView.class));
        setDescription(IcoMoonToEnumService.DESCRIPTION);
    }
}
