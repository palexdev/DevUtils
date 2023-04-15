package io.github.palexdev.devutils.components;

import io.github.palexdev.devutils.settings.descriptors.SettingDescriptor;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.VPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.function.Supplier;

public class SettingComponent extends GridPane {
    //================================================================================
    // Properties
    //================================================================================
    private final SettingDescriptor descriptor;
    private final ReadOnlyStringWrapper initialValue = new ReadOnlyStringWrapper();
    private final ReadOnlyBooleanWrapper changed = new ReadOnlyBooleanWrapper(false);
    private boolean resetOnEmpty = true;

    //================================================================================
    // Constructors
    //================================================================================
    private SettingComponent(SettingDescriptor descriptor) {
        this.descriptor = descriptor;
        setInitialValue(descriptor.setting().get());
        changed.bind(descriptor.setting().valueProperty().isNotEqualTo(initialValue));
    }

    //================================================================================
    // Methods
    //================================================================================
    public void reset() {
        descriptor.setting().reset();
    }

    //================================================================================
    // Builder Methods
    //================================================================================
    public static SettingComponent bool(SettingDescriptor sd) {
        SettingComponent sc = new SettingComponent(sd);
        MFXCheckbox check = new MFXCheckbox(sd.description());
        check.setSelected(Boolean.parseBoolean(sc.getInitialValue()));
        check.selectedProperty().addListener((ob, o, n) -> sd.setting().set(n));
        MFXIconWrapper reset = sc.buildResetIcon();
        reset.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> check.setSelected(Boolean.parseBoolean(sc.getInitialValue())));
        sc.add(check, 0, 0);
        sc.add(reset, 1, 0);
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow(Priority.ALWAYS);
        sc.getColumnConstraints().add(cc0);
        return sc;
    }

    public static SettingComponent string(SettingDescriptor sd) {
        return createFieldComponent(sd, FloatingField::new);
    }

    public static SettingComponent number(SettingDescriptor sd) {
        return createFieldComponent(sd, NumberField::new);
    }

    private static SettingComponent createFieldComponent(SettingDescriptor sd, Supplier<FloatingField> fieldFactory) {
        SettingComponent sc = new SettingComponent(sd);
        sc.setResetOnEmpty(sd.avoidEmpty());
        FloatingField field = fieldFactory.get();
        field.setFloatingText(sd.description());
        field.field().setText(sc.getInitialValue());
        field.focusWithinProperty().addListener((ob, o, n) -> {
            if (!n) {
                String text = field.field().getText();
                if (text.isBlank() && sc.isResetOnEmpty()) {
                    sd.setting().reset();
                    field.field().setText(sd.setting().get());
                    return;
                }
                sd.setting().set(text);
            }
        });
        MFXIconWrapper reset = sc.buildResetIcon();
        reset.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            sc.reset();
            field.field().setText(sc.getInitialValue());
        });
        sc.add(field, 0, 0);
        sc.add(reset, 1, 0);
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow(Priority.ALWAYS);
        sc.getColumnConstraints().add(cc0);
        return sc;
    }

    private MFXIconWrapper buildResetIcon() {
        MFXIconWrapper icon = new MFXIconWrapper()
            .setIcon("fas-arrow-rotate-left")
            .makeRound(true)
            .enableRippleGenerator(true)
            .setSize(32);
        icon.getStyleClass().add("undo-icon");
        icon.visibleProperty().bind(changedProperty());
        GridPane.setValignment(icon, VPos.CENTER);
        return icon;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public SettingDescriptor getDescriptor() {
        return descriptor;
    }

    public String getInitialValue() {
        return initialValue.get();
    }

    public ReadOnlyStringProperty initialValueProperty() {
        return initialValue.getReadOnlyProperty();
    }

    private void setInitialValue(String initialValue) {
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

    public SettingComponent setResetOnEmpty(boolean resetOnEmpty) {
        this.resetOnEmpty = resetOnEmpty;
        return this;
    }
}
