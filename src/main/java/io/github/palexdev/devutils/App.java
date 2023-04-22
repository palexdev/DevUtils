package io.github.palexdev.devutils;

import io.github.palexdev.devutils.events.AppCloseEvent;
import io.github.palexdev.devutils.events.ResetSettingsEvent;
import io.github.palexdev.devutils.events.StageReadyEvent;
import io.github.palexdev.devutils.settings.AppSettings;
import io.github.palexdev.devutils.settings.base.Settings;
import io.github.palexdev.devutils.views.DashboardView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

public class App extends Application {
    private Stage stage;
    private AppSettings settings;
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
/*        context = new SpringApplicationBuilder(SpringEntry.class).run();

        // Allows MainView (custom Stage) to tell the App to close
        ApplicationListener<AppCloseEvent> closeHandler = e -> stop();
        context.addApplicationListener(closeHandler);

        settings = context.getBean(AppSettings.class);
        if (settings.isResetSettings()) context.publishEvent(new ResetSettingsEvent(Settings.class));*/
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Init Context
        context = new SpringApplicationBuilder(SpringEntry.class)
            .initializers(c -> c.getBeanFactory().registerSingleton("stage", stage))
            .initializers(SpringHelper::setContext)
            .run();

        // Allows MainView (custom Stage) to tell the App to close
        ApplicationListener<AppCloseEvent> closeHandler = e -> stop();
        context.addApplicationListener(closeHandler);

        settings = context.getBean(AppSettings.class);
        if (settings.isResetSettings()) context.publishEvent(new ResetSettingsEvent(Settings.class));

        // Init App
        context.publishEvent(new StageReadyEvent(stage));
        SpringHelper.setView(DashboardView.class);
    }

    @Override
    public void stop() {
        settings.windowWidth.set(stage.getWidth());
        settings.windowHeight.set(stage.getHeight());
        context.stop();
        Platform.exit();
    }
}
