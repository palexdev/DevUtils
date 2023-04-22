package io.github.palexdev.devutils.events;

import org.springframework.context.ApplicationEvent;

public class AppCloseEvent extends ApplicationEvent {

    public AppCloseEvent(Object source) {
        super(source);
    }
}
