package io.github.palexdev.devutils.services;

import io.github.palexdev.devutils.components.ServiceCard;
import io.github.palexdev.devutils.model.IcoMoonToEnumModel;
import io.github.palexdev.devutils.views.IcoMoonToEnumView;
import io.github.palexdev.devutils.views.MainView;
import io.github.palexdev.devutils.views.settings.IcoMoonToEnumSettingsView;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;

public class IcoMoonToEnumService extends Service<IcoMoonToEnumModel> {
    //================================================================================
    // Singleton
    //================================================================================
    private static IcoMoonToEnumService instance;

    public static IcoMoonToEnumService get() {
        if (instance == null) instance = new IcoMoonToEnumService();
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private final IcoMoonToEnumModel model;

    //================================================================================
    // Constructors
    //================================================================================
    private IcoMoonToEnumService() {
        this.model = new IcoMoonToEnumModel();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public IcoMoonToEnumView view() {
        return IcoMoonToEnumView.get();
    }

    @Override
    public ServiceCard toCard() {
        if (card == null) {
            card = new ServiceCard(
                "IcoMoon To Enum Converter",
                new MFXFontIcon("fas-icons")
            );
            card.setAction(() -> MainView.get().setView(IcoMoonToEnumView.get()));
            card.setSettingsAction(() -> MainView.get().setView(IcoMoonToEnumSettingsView.get()));
            card.setDescription(description());
        }
        return card;
    }

    @Override
    public IcoMoonToEnumModel model() {
        return model;
    }

    @Override
    public String description() {
        return ServicesDescriptions.ICOMOON_SERVICE;
    }
}
