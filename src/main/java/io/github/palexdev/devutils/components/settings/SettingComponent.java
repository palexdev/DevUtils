package io.github.palexdev.devutils.components.settings;

/*
public class SettingComponent<T> extends GridPane {
    //================================================================================
    // Properties
    //================================================================================
    private final SettingDescriptor<T> descriptor;
    private final ReadOnlyObjectWrapper<T> initialValue = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyBooleanWrapper changed = new ReadOnlyBooleanWrapper(false);
    private boolean resetOnEmpty = true;

    //================================================================================
    // Constructors
    //================================================================================
    public SettingComponent(SettingDescriptor<T> descriptor) {
        this.descriptor = descriptor;
        setInitialValue(descriptor.setting().get());
        descriptor.settings().onChange(e -> {
            String key = e.getKey();
            String settingKey = descriptor.setting().getName();
            if (!Objects.equals(key, settingKey)) return;
            setChanged(!Objects.equals(e.getNewValue(), getInitialValue()));
        });
    }

    //================================================================================
    // Methods
    //================================================================================
    public void reset() {
        descriptor.setting().reset();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public SettingDescriptor<T> getDescriptor() {
        return descriptor;
    }

    public T getInitialValue() {
        return initialValue.get();
    }

    public ReadOnlyObjectProperty<T> initialValueProperty() {
        return initialValue.getReadOnlyProperty();
    }

    private void setInitialValue(T initialValue) {
        this.initialValue.set(initialValue);
    }

    public boolean isChanged() {
        return changed.get();
    }

    public ReadOnlyBooleanProperty changedProperty() {
        return changed.getReadOnlyProperty();
    }

    private void setChanged(boolean changed) {
        this.changed.set(changed);
    }

    public boolean isResetOnEmpty() {
        return resetOnEmpty;
    }

    public SettingComponent<T> setResetOnEmpty(boolean resetOnEmpty) {
        this.resetOnEmpty = resetOnEmpty;
        return this;
    }
}
*/

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import io.github.palexdev.materialfx.css.themes.Stylesheets;
import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.prefs.PreferenceChangeListener;

public abstract class SettingComponent<T> extends MFXControl<SettingComponentBehavior<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final SettingDescriptor<T> descriptor;
    private final ReadOnlyObjectWrapper<T> initialValue = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyBooleanWrapper changed = new ReadOnlyBooleanWrapper();

    //================================================================================
    // Constructors
    //================================================================================
    public SettingComponent(SettingDescriptor<T> descriptor) {
        this.descriptor = descriptor;
        setInitialValue(descriptor.setting().get());
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        setBehaviorProvider(defaultBehaviorProvider());
        getStylesheets().addAll(
            Resources.loadComponentCss("SettingComponent.css"),
            Stylesheets.CHECKBOX.loadTheme()
        );
    }

    public void reset() {
        getBehavior().reset(null);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("setting-component");
    }

    @Override
    public Supplier<SettingComponentBehavior<T>> defaultBehaviorProvider() {
        return () -> new SettingComponentBehavior<>(this);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected static abstract class SkinBase<T> extends MFXSkinBase<SettingComponent<T>, SettingComponentBehavior<T>> {
        protected MFXIconWrapper resetIcon;
        protected PreferenceChangeListener onSettingChanged;

        protected SkinBase(SettingComponent<T> component) {
            super(component);

            onSettingChanged = e -> {
                String evtKey = e.getKey();
                String descKey = descriptor().setting().name();
                if (!Objects.equals(evtKey, descKey)) return;
                settingChanged();
            };
            descriptor().settings().onChange(onSettingChanged);

            resetIcon = buildResetIcon();
            getChildren().add(resetIcon);
        }

        @Override
        protected void initBehavior(SettingComponentBehavior<T> behavior) {
            behavior.handler(resetIcon, MouseEvent.MOUSE_CLICKED, behavior::reset);
        }

        protected void settingChanged() {
            SettingComponent<T> component = getSkinnable();
            component.setChanged(!Objects.equals(component.getInitialValue(), component.getDescriptor().setting().get()));
        }

        protected MFXIconWrapper buildResetIcon() {
            SettingComponent<T> component = getSkinnable();
            MFXIconWrapper icon = new MFXIconWrapper()
                .setIcon("fas-arrow-rotate-left")
                .makeRound(true)
                .enableRippleGenerator(true)
                .setSize(32);
            icon.getStyleClass().add("undo-icon");
            icon.visibleProperty().bind(component.changedProperty());
            return icon;
        }

        protected SettingDescriptor<T> descriptor() {
            return getSkinnable().getDescriptor();
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            layoutInArea(
                resetIcon,
                x, y, w, h, 0,
                HPos.RIGHT, VPos.CENTER
            );
        }

        @Override
        public void dispose() {
            descriptor().settings().removeOnChange(onSettingChanged);
            onSettingChanged = null;
            super.dispose();
        }
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public SettingDescriptor<T> getDescriptor() {
        return descriptor;
    }

    public T getInitialValue() {
        return initialValue.get();
    }

    public ReadOnlyObjectProperty<T> initialValueProperty() {
        return initialValue.getReadOnlyProperty();
    }

    protected void setInitialValue(T initialValue) {
        this.initialValue.set(initialValue);
    }

    public boolean isChanged() {
        return changed.get();
    }

    public ReadOnlyBooleanProperty changedProperty() {
        return changed;
    }

    protected void setChanged(boolean changed) {
        this.changed.set(changed);
    }
}
