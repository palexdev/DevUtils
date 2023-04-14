package io.github.palexdev.devutils;

import io.github.palexdev.devutils.settings.SettingsDB;
import io.github.palexdev.devutils.views.DashboardView;
import io.github.palexdev.devutils.views.IcoMoonToEnumView;
import io.github.palexdev.devutils.views.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {
    private static final StringProperty title = new SimpleStringProperty("Development Utils");
    private static Stage stage;

    @Override
    public void start(Stage stage) {
        // Init main objects
        App.stage = stage;
        MainView.get();

        // Init Views
        DashboardView.get();
        IcoMoonToEnumView.get();

        // Show
        stage.show();
    }

    @Override
    public void init() {
        Optional.ofNullable(System.getenv("RESET_SETTINGS"))
            .ifPresent(s -> {
                if (Boolean.parseBoolean(s))
                    SettingsDB.resetAll();
            });
    }

    public static void exit() {
        SettingsDB.WINDOW_SIZES.set(String.format("%f,%f", stage.getWidth(), stage.getHeight()));
        Platform.exit();
        System.exit(0);
    }

    public static Stage window() {
        return stage;
    }

    public static String getTitle() {
        return titleProperty().get();
    }

    public static StringProperty titleProperty() {
        return title;
    }

    public static void setTitle(String title) {
        titleProperty().set(title);
    }
}
