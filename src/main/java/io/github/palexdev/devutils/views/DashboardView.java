package io.github.palexdev.devutils.views;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.settings.AppSettings;
import io.github.palexdev.devutils.views.DashboardView.DashboardPane;
import io.github.palexdev.devutils.views.base.View;
import io.github.palexdev.devutils.views.cards.ChangelogGeneratorCard;
import io.github.palexdev.devutils.views.cards.IcoMoonToEnumCard;
import io.github.palexdev.devutils.views.cards.MaterialThemeConverterCard;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.mfxcore.builders.bindings.DoubleBindingBuilder;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

@Component
public class DashboardView extends View<DashboardPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final AppSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    public DashboardView(AppSettings settings) {
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected DashboardPane build() {
        return new DashboardPane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected class DashboardPane extends StackPane {
        private final GridPane root;
        private final MFXScrollPane scrollable;

        public DashboardPane() {
            Label welcome = new Label();
            welcome.getStyleClass().add("header");
            welcome.setMaxHeight(Double.MAX_VALUE);
            Label sub = new Label("What service do you need?");
            sub.getStyleClass().add("sub-header");
            sub.setMaxHeight(Double.MAX_VALUE);

            // Init welcome text and listen for changes
            settings.username.container().onChange(e -> {
                String val = e.getNewValue();
                String text = val.isBlank() ? "Welcome User" : "Welcome back, " + val;
                Platform.runLater(() -> welcome.setText(text)); // Since we are in a Java listener
            });
            String name = settings.username.get();
            welcome.setText((name.isBlank() ? "Welcome User" : "Welcome back, " + name));

            root = new GridPane() {
                @Override
                protected void layoutChildren() {
                    super.layoutChildren();
                    layoutInArea(
                        scrollable,
                        getLayoutX(), getLayoutY(), getWidth(), getHeight(), 0,
                        HPos.CENTER, VPos.TOP
                    );
                }
            };
            root.setId("home-view");
            root.add(welcome, 0, 0);
            GridPane.setColumnSpan(welcome, GridPane.REMAINING);
            root.add(sub, 0, 1);
            GridPane.setColumnSpan(sub, GridPane.REMAINING);

            // Add a separator
            Region reg = new Region();
            reg.getStyleClass().add("grid-separator");
            root.add(reg, 0, 2);
            GridPane.setColumnSpan(reg, GridPane.REMAINING);

            // Build and add Cards
            FlowPane content = new FlowPane(Orientation.HORIZONTAL,
                new IcoMoonToEnumCard(),
                new ChangelogGeneratorCard(),
                new MaterialThemeConverterCard()
            );
            content.prefWrapLengthProperty().bind(DoubleBindingBuilder.build()
                .setMapper(() -> (250.0 + content.getHgap()) * 3)
                .addSources(content.hgapProperty(), content.getChildren())
                .get()
            );
            content.getStyleClass().add("cards");

            scrollable = new MFXScrollPane(content);
            scrollable.setFitToWidth(true);
            scrollable.setFitToHeight(true);
            root.add(scrollable, 0, 3);

            getStylesheets().add(Resources.loadViewCss("DashboardView.css"));
            getChildren().add(root);
        }


    }
}
