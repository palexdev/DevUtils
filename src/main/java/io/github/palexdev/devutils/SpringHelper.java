package io.github.palexdev.devutils;

import io.github.palexdev.devutils.events.AppCloseEvent;
import io.github.palexdev.devutils.events.ViewSwitchEvent;
import io.github.palexdev.devutils.views.base.View;
import org.springframework.context.ApplicationContext;

public class SpringHelper {
    //================================================================================
    // Properties
    //================================================================================
    private static ApplicationContext context;

    //================================================================================
    // Constructors
    //================================================================================
    private SpringHelper() {}

    //================================================================================
    // Static Methods
    //================================================================================
    public static void setView(Class<? extends View<?>> view) {
        context.publishEvent(new ViewSwitchEvent(context.getBean(view)));
    }

    public static void exit(Object source) {
        context.publishEvent(new AppCloseEvent(source));
    }

    public static <T> T getBean(Class<? extends T> k) {
        return context.getBean(k);
    }

    static void setContext(ApplicationContext context) {
        SpringHelper.context = context;
    }
}
