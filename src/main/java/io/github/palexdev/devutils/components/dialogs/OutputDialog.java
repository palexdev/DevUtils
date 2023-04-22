package io.github.palexdev.devutils.components.dialogs;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.TextArea;
import io.github.palexdev.materialfx.css.themes.Theme;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import javafx.event.Event;

public class OutputDialog extends MFXGenericDialog {
    //================================================================================
    // Properties
    //================================================================================
    private String saveDir = System.getProperty("user.home");

    //================================================================================
    // Constructors
    //================================================================================
    public OutputDialog() {
        super();
        getStylesheets().setAll(
            MFXThemeManager.PURPLE_LIGHT.load(),
            Resources.loadComponentCss("OutputDialog.css")
        );
        build();
        CSSFX.start(this);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void build() {
        TextArea ta = new TextArea();
        ta.textProperty().bind(contentTextProperty());
        ta.setOnContextMenuRequested(Event::consume);
        setContent(ta);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Theme getTheme() {
        return null;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }
}
