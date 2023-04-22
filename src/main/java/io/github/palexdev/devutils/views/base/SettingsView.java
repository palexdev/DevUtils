package io.github.palexdev.devutils.views.base;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.scene.input.MouseEvent;

public abstract class SettingsView extends View<IconPane> {

    //================================================================================
    // Abstract Methods
    //================================================================================
    protected abstract void onClose();

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected IconPane build() {
        MFXIconWrapper icon = new MFXIconWrapper()
            .setIcon("fas-xmark");
        icon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> onClose());

        IconPane root = new IconPane();
        root.setIcon(icon);
        root.getStyleClass().add("settings-view");
        root.getStylesheets().add(Resources.loadSettingsCss("SettingsView.css"));
        return root;
    }
}
