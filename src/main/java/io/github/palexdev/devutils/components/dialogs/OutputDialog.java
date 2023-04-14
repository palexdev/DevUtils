package io.github.palexdev.devutils.components.dialogs;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.devutils.App;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.views.IcoMoonToEnumView;
import io.github.palexdev.materialfx.css.themes.Theme;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

import static io.github.palexdev.devutils.settings.SettingsDB.ICOMOON_LAST_SAVE_DIR;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class OutputDialog extends MFXGenericDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty text = new SimpleStringProperty("");

    //================================================================================
    // Constructors
    //================================================================================
    public OutputDialog() {
        super();
        getStylesheets().setAll(
            MFXThemeManager.PURPLE_LIGHT.load(),
            Resources.loadCss("IcoMoonView.css")
        );
        build();
        CSSFX.start(this);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void build() {
        TextArea ta = new TextArea();
        ta.textProperty().bind(text);
        ta.setOnContextMenuRequested(Event::consume);
        setContent(ta);

        MFXButton toClipboard = new MFXFilledButton("Copy to Clipboard");
        toClipboard.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.put(DataFormat.PLAIN_TEXT, ta.getText());
            Clipboard.getSystemClipboard().setContent(content);
            getScene().getWindow().hide();
        });

        MFXButton toFile = new MFXFilledButton("Save to File");
        toFile.setOnAction(e -> {
            saveToFile(ta.getText());
            getScene().getWindow().hide();
        });

        addActions(toClipboard, toFile);
    }

    protected void saveToFile(String text) {
        String lastDir = ICOMOON_LAST_SAVE_DIR.get();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(lastDir));

        File file = fc.showSaveDialog(App.window());
        if (file == null) return;
        try {
            Files.writeString(
                file.toPath(),
                text,
                CREATE,
                TRUNCATE_EXISTING
            );
        } catch (Exception ex) {
            IcoMoonToEnumView.get().error("Failed to save to file because:" + ex.getMessage());
        }
        ICOMOON_LAST_SAVE_DIR.set(file.toPath().getParent());
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Theme getTheme() {
        return null;
    }
}
