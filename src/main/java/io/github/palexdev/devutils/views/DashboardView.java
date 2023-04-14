package io.github.palexdev.devutils.views;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.services.IcoMoonToEnumService;
import io.github.palexdev.mfxcore.builders.bindings.StringBindingBuilder;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import static io.github.palexdev.devutils.settings.SettingsDB.USERNAME;

public class DashboardView extends View {
    //================================================================================
    // Singleton
    //================================================================================
    private static final DashboardView instance = new DashboardView();

    public static DashboardView get() {
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private DashboardViewPane root;

    //================================================================================
    // Constructors
    //================================================================================
    private DashboardView() {
        build();
    }

    //================================================================================
    // Methods
    //================================================================================

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void build() {
        root = new DashboardViewPane();
    }

    @Override
    public Region toRegion() {
        return root;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    private static class DashboardViewPane extends StackPane {

        public DashboardViewPane() {
            Label welcome = new Label();
            welcome.textProperty().bind(StringBindingBuilder.build()
                .setMapper(() -> {
                    String val = USERNAME.get();
                    if (val.isBlank()) return "Welcome User";
                    return "Welcome back, " + val;
                })
                .addSources(USERNAME.valueProperty())
                .get());
            welcome.getStyleClass().add("header");
            welcome.setMaxHeight(Double.MAX_VALUE);
            Label sub = new Label("What service do you need?");
            sub.getStyleClass().add("sub-header");
            sub.setMaxHeight(Double.MAX_VALUE);

            GridPane root = new GridPane();
            root.setId("home-view");
            root.add(welcome, 0, 0);
            GridPane.setColumnSpan(welcome, GridPane.REMAINING);
            root.add(sub, 0, 1);
            GridPane.setColumnSpan(sub, GridPane.REMAINING);

            // Add empty separator
            Region reg = new Region();
            reg.getStyleClass().add("grid-separator");
            root.add(reg, 0, 2);
            GridPane.setColumnSpan(reg, GridPane.REMAINING);

            // Add Cards
            root.add(IcoMoonToEnumService.get().toCard(), 0, 3);

            getStylesheets().add(Resources.loadCss("DashboardView.css"));
            getChildren().add(root);
        }
    }
}
