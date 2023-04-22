package io.github.palexdev.devutils.events;

import io.github.palexdev.devutils.settings.base.Settings;
import org.springframework.context.ApplicationEvent;

public class ResetSettingsEvent extends ApplicationEvent {

    public ResetSettingsEvent(Class<? extends Settings> sClass) {
        super(sClass);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Settings> getSettingsClass() {
        return (Class<? extends Settings>) super.getSource();
    }
}
