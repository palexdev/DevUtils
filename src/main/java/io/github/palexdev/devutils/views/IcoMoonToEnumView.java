package io.github.palexdev.devutils.views;

import io.github.palexdev.devutils.App;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.FloatingField;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.LogView;
import io.github.palexdev.devutils.components.LogView.Log;
import io.github.palexdev.devutils.components.LogView.LogType;
import io.github.palexdev.devutils.components.dialogs.OutputDialog;
import io.github.palexdev.devutils.model.IcoMoonToEnumModel;
import io.github.palexdev.devutils.services.IcoMoonToEnumService;
import io.github.palexdev.devutils.views.settings.IcoMoonToEnumSettingsView;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.palexdev.devutils.settings.SettingsDB.ICOMOON_LAST_OPEN_DIR;
import static io.github.palexdev.devutils.settings.SettingsDB.ICOMOON_LAST_SAVE_DIR;
import static java.nio.file.StandardOpenOption.*;

public class IcoMoonToEnumView extends View {
    //================================================================================
    // Singleton
    //================================================================================
    private static final IcoMoonToEnumView instance = new IcoMoonToEnumView();

    public static IcoMoonToEnumView get() {
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private IcoMoonToEnumViewPane root;
    private final ObservableList<Log> logs = FXCollections.observableArrayList();

    //================================================================================
    // Constructors
    //================================================================================
    private IcoMoonToEnumView() {
        build();
    }

    //================================================================================
    // Methods
    //================================================================================
    public void resetLogs() {
        logs.clear();
        logGeneric("---------- IcoMoonToEnum Logging ----------");
        logGeneric("");
    }

    public void logGeneric(String message) {
        logs.add(Log.of(LogType.GENERIC, message));
    }

    public void error(String message) {
        logs.add(Log.of(LogType.ERROR, message));
    }

    public void warn(String message) {
        logs.add(Log.of(LogType.WARN, message));
    }

    public void ok(String message) {
        logs.add(Log.of(LogType.OK, message));
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void build() {
        root = new IcoMoonToEnumViewPane();
    }

    @Override
    public Region toRegion() {
        return root;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    private class IcoMoonToEnumViewPane extends IconPane {
        private final GridPane grid;
        private final LogView logView;
        private final OutputDialog od;
        private final MFXStageDialog sd;

        private final MFXIconWrapper stIcon;

        public IcoMoonToEnumViewPane() {
            IcoMoonToEnumModel model = IcoMoonToEnumService.get().model();

            logView = new LogView();
            Bindings.bindContent(logView.getLogs(), logs);
            resetLogs();

            grid = new GridPane();
            grid.getStyleClass().add("grid");

            // Build and init options/fields
            VBox container = new VBox(10);
            Label title = new Label("File Info");
            title.setMaxWidth(Double.MAX_VALUE);
            title.getStyleClass().add("title");
            FloatingField name = FloatingField.asLabel("", "File Name");
            name.field().textProperty().bind(model.nameProperty());
            FloatingField path = FloatingField.asLabel("", "File Path");
            path.field().textProperty().bind(model.pathProperty());
            FloatingField size = FloatingField.asLabel("", "File Size");
            size.field().textProperty().bind(model.sizeProperty());
            FloatingField type = FloatingField.asLabel("", "File Type");
            type.field().textProperty().bind(model.typeProperty());
            FloatingField lines = FloatingField.asLabel("", "File Lines");
            lines.field().textProperty().bind(model.linesProperty());
            Label valid = new Label(
                "The chosen file is valid. Ready to parse!",
                new MFXFontIcon("fas-circle-check")
            );
            valid.getStyleClass().add("valid-label");
            valid.visibleProperty().bind(model.validProperty());
            container.getChildren().addAll(title, name, path, size, type, lines, valid);
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

            file.setOnAction(e -> chooseFile());
            parse.setOnAction(e -> parse());
            logs.setOnAction(e -> saveLogs());
            reset.setOnAction(e -> reset());

            // Init Grid
            ColumnConstraints cc0 = new ColumnConstraints();
            cc0.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc0);
            grid.add(logView, 0, 0);
            grid.add(container, 1, 0);
            grid.add(box, 0, 1);

            // Build Dialogs
            od = new OutputDialog();
            sd = MFXGenericDialogBuilder.build(od)
                .setShowAlwaysOnTop(false)
                .setShowMinimize(false)
                .toStageDialogBuilder()
                .initModality(Modality.WINDOW_MODAL)
                .initOwner(App.window())
                .setOwnerNode((Pane) App.window().getScene().getRoot())
                .setCenterInOwnerNode(true)
                .setScrimOwner(true)
                .setDraggable(true)
                .get();

            // Settings Icon
            stIcon = new MFXIconWrapper()
                .setIcon("fas-gear")
                .makeRound(true);
            stIcon.setSize(32);
            stIcon.getStyleClass().add("st-icon");
            stIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> MainView.get().setView(IcoMoonToEnumSettingsView.get()));

            getStyleClass().add("icomoon-view");
            getStylesheets().add(Resources.loadCss("IcoMoonView.css"));
            setIcon(stIcon);
            setContent(grid);
        }

        protected void chooseFile() {
            String initPath = ICOMOON_LAST_OPEN_DIR.get();
            FileChooser fc = new FileChooser();
            if (Files.exists(Path.of(initPath))) fc.setInitialDirectory(new File(initPath));

            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("SVG font resource", "*.svg");
            fc.getExtensionFilters().add(filter);
            File file = fc.showOpenDialog(App.window());
            if (file == null) return;
            IcoMoonToEnumService.get().model().setFile(file);
            ICOMOON_LAST_OPEN_DIR.set(file.toPath().getParent());
        }

        protected void parse() {
            IcoMoonToEnumModel model = IcoMoonToEnumService.get().model();
            if (!model.isValid()) return;
            String parsed = model.parse();
            showOutputDialog(parsed);
        }

        protected void saveLogs() {
            String lastDir = ICOMOON_LAST_SAVE_DIR.get();
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(lastDir));

            File file = fc.showSaveDialog(App.window());
            if (file == null) return;

            ObservableList<LogView.Log> logs = logView.getLogs();
            try {
                Path path = file.toPath();
                Files.writeString(path, "", CREATE, TRUNCATE_EXISTING);
                for (LogView.Log log : logs) {
                    Files.writeString(path, log.toString() + "\n", APPEND);
                }
            } catch (IOException ex) {
                error("Failed to save logs because: " + ex.getMessage());
            }
        }

        protected void reset() {
            IcoMoonToEnumModel model = IcoMoonToEnumService.get().model();
            model.setFile(null);
        }

        protected void showOutputDialog(String output) {
            od.setText(output);
            sd.showDialog();
        }
    }
}
