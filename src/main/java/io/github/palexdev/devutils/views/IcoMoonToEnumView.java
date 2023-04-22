package io.github.palexdev.devutils.views;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.beans.Log;
import io.github.palexdev.devutils.components.FloatingField;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.LogView;
import io.github.palexdev.devutils.components.dialogs.DialogsServiceBase;
import io.github.palexdev.devutils.components.dialogs.DialogsServiceBase.DialogConfig;
import io.github.palexdev.devutils.components.dialogs.OutputDialog;
import io.github.palexdev.devutils.services.IcoMoonToEnumService;
import io.github.palexdev.devutils.settings.IcoMoonToEnumSettings;
import io.github.palexdev.devutils.views.IcoMoonToEnumView.IcoMoonPane;
import io.github.palexdev.devutils.views.base.View;
import io.github.palexdev.devutils.views.settings.IcoMoonToEnumSettingsView;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Component
public class IcoMoonToEnumView extends View<IcoMoonPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final Stage stage;
    private final IcoMoonToEnumService service;
    private final IcoMoonToEnumSettings settings;
    private final DialogsServiceBase dialogsService;

    //================================================================================
    // Constructors
    //================================================================================
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public IcoMoonToEnumView(Stage stage, IcoMoonToEnumService service, IcoMoonToEnumSettings settings, DialogsServiceBase dialogsService) {
        this.stage = stage;
        this.service = service;
        this.settings = settings;
        this.dialogsService = dialogsService;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected IcoMoonPane build() {
        return new IcoMoonPane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected class IcoMoonPane extends IconPane {
        private final GridPane grid;
        private final LogView logView;
        private final DialogConfig<OutputDialog> dialogConfig;

        private final MFXIconWrapper stIcon;

        public IcoMoonPane() {
            logView = new LogView();
            Bindings.bindContent(logView.getLogs(), service.getLogs());
            service.resetLogs();

            grid = new GridPane();
            grid.getStyleClass().add("grid");

            // Build and init options/fields
            VBox container = new VBox(10);
            Label title = new Label("File Info");
            title.setMaxWidth(Double.MAX_VALUE);
            title.getStyleClass().add("title");
            FloatingField name = FloatingField.asLabel("", "File Name");
            name.field().textProperty().bind(service.nameProperty());
            FloatingField path = FloatingField.asLabel("", "File Path");
            path.field().textProperty().bind(service.pathProperty());
            FloatingField size = FloatingField.asLabel("", "File Size");
            size.field().textProperty().bind(service.sizeProperty());
            FloatingField type = FloatingField.asLabel("", "File Type");
            type.field().textProperty().bind(service.typeProperty());
            FloatingField nLines = FloatingField.asLabel("", "File Lines");
            nLines.field().textProperty().bind(service.nLinesProperty());
            Label valid = new Label(
                "The chosen file is valid. Ready to parse!",
                new MFXFontIcon("fas-circle-check")
            );
            valid.getStyleClass().add("valid-label");
            valid.visibleProperty().bind(service.validProperty());
            container.getChildren().addAll(title, name, path, size, type, nLines, valid);
            container.setAlignment(Pos.TOP_CENTER);

            // Build and init actions
            MFXButton file = new MFXFilledButton("Choose File");
            MFXButton parse = new MFXFilledButton("Parse");
            MFXButton logs = new MFXFilledButton("Save Logs");
            MFXButton reset = new MFXFilledButton("Reset");
            HBox box = new HBox(20, file, parse, logs, reset);
            box.setAlignment(Pos.BOTTOM_RIGHT);
            box.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(box, Priority.ALWAYS);
            GridPane.setColumnSpan(box, GridPane.REMAINING);

            file.setOnAction(e -> openFile());
            parse.setOnAction(e -> parse());
            logs.setOnAction(e -> saveLogs());
            reset.setOnAction(e -> service.setFile(null));

            // Init Grid
            ColumnConstraints cc0 = new ColumnConstraints();
            cc0.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc0);
            grid.add(logView, 0, 0);
            grid.add(container, 1, 0);
            grid.add(box, 0, 1);

            // Build Dialogs
            dialogConfig = new DialogConfig<OutputDialog>()
                .setOnConfigure(o -> {
                    o.setContentText(service.parse());
                    o.setSaveDir(settings.lastSaveDir.get());
                    buildOutDialogActions(o);
                })
                .setShowAlwaysOnTop(false)
                .setShowMinimize(false)
                .setModality(Modality.WINDOW_MODAL)
                .setOwner(stage)
                .setOwnerNode(this)
                .setCenterInOwnerNode(true)
                .setScrimOwner(true)
                .setDraggable(true);

            // Settings Icon
            stIcon = new MFXIconWrapper()
                .setIcon("fas-gear")
                .makeRound(true);
            stIcon.setSize(32);
            stIcon.getStyleClass().add("st-icon");
            stIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> SpringHelper.setView(IcoMoonToEnumSettingsView.class));

            getStyleClass().add("icomoon-view");
            getStylesheets().add(Resources.loadViewCss("IcoMoonToEnumView.css"));
            setIcon(stIcon);
            setContent(grid);
        }

        protected void buildOutDialogActions(OutputDialog content) {
            MFXButton toClipboard = new MFXFilledButton("Copy to Clipboard");
            toClipboard.setOnAction(e -> {
                ClipboardContent cc = new ClipboardContent();
                cc.put(DataFormat.PLAIN_TEXT, content.getContentText());
                Clipboard.getSystemClipboard().setContent(cc);
                content.getScene().getWindow().hide();
            });
            MFXButton toFile = new MFXFilledButton("Save to File");
            toFile.setOnAction(e -> {
                saveToFile(content.getContentText());
                content.getScene().getWindow().hide();
            });
            content.addActions(toClipboard, toFile);
        }

        protected void parse() {
            if (!service.isValid()) return;
            dialogsService.showOutput(IcoMoonToEnumView.class, dialogConfig);
        }

        protected void openFile() {
            String openDir = settings.lastOpenDir.get();
            FileChooser fc = new FileChooser();
            if (Files.exists(Path.of(openDir))) fc.setInitialDirectory(new File(openDir));

            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("SVG font resource", "*.svg");
            fc.getExtensionFilters().add(filter);
            File file = fc.showOpenDialog(stage);
            if (file == null) return;
            service.setFile(file);
            settings.lastOpenDir.set(String.valueOf(file.toPath().getParent()));
        }

        protected void saveToFile(String text) {
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
                service.error("Failed to save to file because: " + ex.getMessage());
            }
            settings.lastSaveDir.set(String.valueOf(file.toPath().getParent()));
        }

        protected void saveLogs() {
            StringBuilder sb = new StringBuilder();
            for (Log log : service.getLogs()) {
                sb.append(log.toString()).append("\n");
            }
            saveToFile(sb.toString());
        }
    }
}
