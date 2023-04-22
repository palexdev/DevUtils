package io.github.palexdev.devutils.events;

import io.github.palexdev.devutils.views.base.View;
import org.springframework.context.ApplicationEvent;

public class ViewSwitchEvent extends ApplicationEvent {

    public ViewSwitchEvent(View<?> view) {
        super(view);
    }

    public View<?> getView() {
        return (View<?>) super.getSource();
    }
}
