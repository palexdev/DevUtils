package io.github.palexdev.devutils.views;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.components.IconPane;
import io.github.palexdev.devutils.components.TextArea;
import io.github.palexdev.devutils.services.ChangelogGeneratorService;
import io.github.palexdev.devutils.views.ChangelogGeneratorView.ChangelogGeneratorPane;
import io.github.palexdev.devutils.views.base.View;
import io.github.palexdev.devutils.views.settings.ChangelogGeneratorSettingsView;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.geometry.Pos;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.springframework.stereotype.Component;

@Component
public class ChangelogGeneratorView extends View<ChangelogGeneratorPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final ChangelogGeneratorService service;

    //================================================================================
    // Constructors
    //================================================================================
    public ChangelogGeneratorView(ChangelogGeneratorService service) {
        this.service = service;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected ChangelogGeneratorPane build() {
        return new ChangelogGeneratorPane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected class ChangelogGeneratorPane extends IconPane {
        private final GridPane grid;
        private final MFXIconWrapper stIcon;

        public ChangelogGeneratorPane() {
            grid = new GridPane();
            grid.getStyleClass().add("grid");

            // Fields for input/output
            TextArea left = new TextArea();
            service.changelogProperty().bind(left.textProperty());
            left.setPrefColumnCount(120);
            left.setWrapText(true);
            TextArea right = new TextArea();
            right.textProperty().bind(service.outputProperty());
            right.setEditable(false);
            right.setPrefColumnCount(120);
            right.setWrapText(true);

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

            generate.setOnAction(e -> service.parse());
            copy.setOnAction(e -> {
                service.parse();
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

            // Settings Icon
            stIcon = new MFXIconWrapper()
                .setIcon("fas-gear")
                .makeRound(true);
            stIcon.setSize(32);
            stIcon.getStyleClass().add("st-icon");
            stIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> SpringHelper.setView(ChangelogGeneratorSettingsView.class));

            getStyleClass().add("changelog-view");
            getStylesheets().add(Resources.loadViewCss("ChangelogGeneratorView.css"));
            setContent(grid);
            setIcon(stIcon);
        }
    }
}
