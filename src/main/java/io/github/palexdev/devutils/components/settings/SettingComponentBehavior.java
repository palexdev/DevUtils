package io.github.palexdev.devutils.components.settings;

import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SettingComponentBehavior<T> extends BehaviorBase<SettingComponent<T>> {

    public SettingComponentBehavior(SettingComponent<T> component) {
        super(component);
    }

    public void reset(MouseEvent event) {
        // Null events can come from the component's delegate method
        if (event == null || event.getButton() == MouseButton.PRIMARY) {
            SettingComponent<T> component = getNode();
            component.getDescriptor().setting().reset();
        }
    }
}
