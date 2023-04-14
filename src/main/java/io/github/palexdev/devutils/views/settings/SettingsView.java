package io.github.palexdev.devutils.views.settings;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.views.View;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class SettingsView extends View {
    //================================================================================
    // Properties
    //================================================================================
    protected IconPane root;
    protected Runnable onClose = () -> {};

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void build() {
        MFXIconWrapper close = new MFXIconWrapper();
        close.setIcon("fas-xmark");
        close.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> onClose.run());

        root = new IconPane();
        root.setIcon(close);
        root.getStyleClass().add("settings-view");
        root.getStylesheets().add(Resources.loadCss("SettingsView.css"));
    }

    @Override
    public Region toRegion() {
        return root;
    }
}
