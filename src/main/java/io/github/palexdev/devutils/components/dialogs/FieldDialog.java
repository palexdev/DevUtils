package io.github.palexdev.devutils.components.dialogs;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.FloatingField;
import io.github.palexdev.materialfx.css.themes.Theme;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FieldDialog extends MFXGenericDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty value = new SimpleStringProperty("");

    //================================================================================
    // Constructors
    //================================================================================
    public FieldDialog() {
        super();
        getStylesheets().setAll(
            MFXThemeManager.PURPLE_LIGHT.load(),
            Resources.loadCss("FloatingField.css"),
            Resources.loadCss("FieldDialog.css")
        );
        build();
        CSSFX.start(this);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void build() {
        FloatingField ff = new FloatingField();
        ff.floatingTextProperty().bind(contentTextProperty());
        ff.field().textProperty().bindBidirectional(value);
        setContent(ff);

        MFXButton confirm = new MFXFilledButton("OK");
        confirm.setOnAction(e -> getScene().getWindow().hide());
        addActions(confirm);
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
    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
