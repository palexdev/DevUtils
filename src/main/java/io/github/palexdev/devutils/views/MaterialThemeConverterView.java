package io.github.palexdev.devutils.views;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.TextArea;
import io.github.palexdev.devutils.services.MaterialThemeConverterService;
import io.github.palexdev.devutils.settings.MaterialThemeConverterSettings;
import io.github.palexdev.devutils.views.MaterialThemeConverterView.MaterialThemeConverterPane;
import io.github.palexdev.devutils.views.base.View;
import io.github.palexdev.devutils.views.settings.MaterialThemeConverterSettingsView;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.geometry.Pos;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Component
public class MaterialThemeConverterView extends View<MaterialThemeConverterPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final Stage stage;
    private final MaterialThemeConverterService service;
    private final MaterialThemeConverterSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MaterialThemeConverterView(Stage stage, MaterialThemeConverterService service, MaterialThemeConverterSettings settings) {
        this.stage = stage;
        this.service = service;
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MaterialThemeConverterPane build() {
        return new MaterialThemeConverterPane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected class MaterialThemeConverterPane extends IconPane {
        private final GridPane grid;
        private final MFXIconWrapper stIcon;

        public MaterialThemeConverterPane() {
            grid = new GridPane();
            grid.getStyleClass().add("grid");

            // Output view
            TextArea out = new TextArea();
            out.setEditable(false);
            out.textProperty().bind(service.outputProperty());
            GridPane.setHgrow(out, Priority.ALWAYS);
            GridPane.setVgrow(out, Priority.ALWAYS);

            // Actions
            MFXButton parse = new MFXFilledButton("Parse");
            MFXButton copy = new MFXFilledButton("Parse & Copy");
            MFXButton save = new MFXFilledButton("Parse & Save");
            MFXButton reset = new MFXFilledButton("Reset");
            HBox box = new HBox(20, parse, copy, save, reset);
            box.setAlignment(Pos.BOTTOM_RIGHT);

            parse.setOnAction(e -> parse());
            copy.setOnAction(e -> {
                parse();
                ClipboardContent cc = new ClipboardContent();
                cc.put(DataFormat.PLAIN_TEXT, out.getText());
                Clipboard.getSystemClipboard().setContent(cc);
            });
            save.setOnAction(e -> save(out.getText()));
            reset.setOnAction(e -> service.setFile(null));

            // Init Grid
            grid.add(out, 0, 0);
            grid.add(box, 0, 1);

            // Settings Icon
            stIcon = new MFXIconWrapper()
                .setIcon("fas-gear")
                .makeRound(true);
            stIcon.setSize(32);
            stIcon.getStyleClass().add("st-icon");
            stIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> SpringHelper.setView(MaterialThemeConverterSettingsView.class));

            getStyleClass().add("mtconverter-view");
            getStylesheets().add(Resources.loadViewCss("MaterialThemeConverterView.css"));
            setIcon(stIcon);
            setContent(grid);
        }

        protected void parse() {
            File file = service.getFile();
            String output = service.getOutput();
            if (file != null) {
                if (output.isBlank()) {
                    service.parse();
                }
                return;
            }

            String openDir = settings.lastOpenDir.get();
            FileChooser fc = new FileChooser();
            if (Files.exists(Path.of(openDir))) fc.setInitialDirectory(new File(openDir));

            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSS", "*.css");
            fc.getExtensionFilters().add(filter);
            File chosen = fc.showOpenDialog(stage);
            if (chosen == null) return;
            service.setFile(chosen);
            service.parse();
            settings.lastOpenDir.set(String.valueOf(chosen.toPath().getParent()));
        }

        protected void save(String text) {
            String saveDir = settings.lastSaveDir.get();
            FileChooser fc = new FileChooser();
            if (Files.exists(Path.of(saveDir))) fc.setInitialDirectory(new File(saveDir));

            File file = fc.showSaveDialog(stage);
            if (file == null) return;

            try {
                Files.writeString(
                    file.toPath(),
                    text,
                    CREATE,
                    TRUNCATE_EXISTING
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            settings.lastSaveDir.set(String.valueOf(file.toPath().getParent()));
        }
    }
}
