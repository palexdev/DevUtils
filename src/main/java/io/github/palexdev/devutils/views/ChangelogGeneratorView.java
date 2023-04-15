package io.github.palexdev.devutils.views;

import io.github.palexdev.devutils.App;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.TextArea;
import io.github.palexdev.devutils.components.dialogs.ChoiceDialog;
import io.github.palexdev.devutils.components.dialogs.FieldDialog;
import io.github.palexdev.devutils.enums.ChangeType;
import io.github.palexdev.devutils.model.ChangelogGeneratorModel;
import io.github.palexdev.devutils.services.ChangelogGeneratorService;
import io.github.palexdev.devutils.views.settings.ChangelogGeneratorSettingsView;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialogBuilder;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.geometry.Pos;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;

public class ChangelogGeneratorView extends View {
    //================================================================================
    // Singleton
    //================================================================================
    private static final ChangelogGeneratorView instance = new ChangelogGeneratorView();

    public static ChangelogGeneratorView get() {
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private ChangelogGeneratorViewPane root;

    //================================================================================
    // Constructors
    //================================================================================
    private ChangelogGeneratorView() {
        build();
    }

    //================================================================================
    // Methods
    //================================================================================
    public ChangeType askType() {
        return root.askType();
    }

    public String askVersion() {
        return root.askVersion();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void build() {
        root = new ChangelogGeneratorViewPane();
    }

    @Override
    public Region toRegion() {
        return root;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    private static class ChangelogGeneratorViewPane extends IconPane {
        private final GridPane grid;
        private final ChoiceDialog<ChangeType> cd;
        private final FieldDialog fd;
        private final MFXStageDialog sd;

        private final MFXIconWrapper stIcon;

        public ChangelogGeneratorViewPane() {
            ChangelogGeneratorModel model = ChangelogGeneratorService.get().model();

            grid = new GridPane();
            grid.getStyleClass().add("grid");

            // Fields for input/output
            TextArea left = new TextArea();
            left.textProperty().addListener(i -> model.setChangelog(left.getText()));
            TextArea right = new TextArea();
            right.textProperty().bind(model.parsedProperty());
            right.setEditable(false);

            left.setMaxHeight(Double.MAX_VALUE);
            right.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(left, Priority.ALWAYS);
            GridPane.setHgrow(right, Priority.ALWAYS);

            // Actions
            MFXButton generate = new MFXFilledButton("Generate");
            MFXButton copy = new MFXFilledButton("Generate & Copy");
            MFXButton reset = new MFXFilledButton("Reset");
            HBox box = new HBox(20, generate, copy, reset);
            box.setAlignment(Pos.BOTTOM_RIGHT);
            box.setMaxHeight(Double.MAX_VALUE);
            GridPane.setColumnSpan(box, GridPane.REMAINING);

            generate.setOnAction(e -> model.parse());
            copy.setOnAction(e -> {
                if (model.getParsed().isBlank()) model.parse();
                ClipboardContent cc = new ClipboardContent();
                cc.put(DataFormat.PLAIN_TEXT, right.getText());
                Clipboard.getSystemClipboard().setContent(cc);
            });
            reset.setOnAction(e -> left.setText(""));

            // Init Grid
            for (int i = 0; i < 2; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setHgrow(Priority.ALWAYS);
                grid.getColumnConstraints().add(cc);
            }
            grid.add(left, 0, 0);
            grid.add(right, 1, 0);
            grid.add(box, 0, 1);

            // Build Dialogs
            cd = new ChoiceDialog<>();
            cd.setItems(ChangeType.values());
            cd.getItems().remove(ChangeType.VERSION); // This is unnecessary
            cd.descriptionProperty().bind(model.currentLineProperty());
            cd.setContentText("Change Type");
            cd.setSelection(ChangeType.IGNORE);
            finalizeDialog(cd);

            fd = new FieldDialog();
            fd.setContentText("Input Version");
            finalizeDialog(fd);

            sd = MFXStageDialogBuilder.build()
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
            stIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> MainView.get().setView(ChangelogGeneratorSettingsView.get()));

            getStyleClass().add("changelog-view");
            getStylesheets().add(Resources.loadCss("ChangelogView.css"));
            setContent(grid);
            setIcon(stIcon);
        }

        ChangeType askType() {
            cd.setSelection(ChangeType.IGNORE);
            sd.setContent(cd);
            sd.showAndWait();
            return cd.getSelection();
        }

        String askVersion() {
            fd.setValue("");
            sd.setContent(fd);
            sd.showAndWait();
            return fd.getValue();
        }

        protected void finalizeDialog(MFXGenericDialog d) {
            d.setShowAlwaysOnTop(false);
            d.setShowMinimize(false);
            d.setShowClose(false);
        }
    }
}
