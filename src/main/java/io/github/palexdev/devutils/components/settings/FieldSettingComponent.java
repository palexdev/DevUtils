package io.github.palexdev.devutils.components.settings;

import io.github.palexdev.devutils.components.FloatingField;
import io.github.palexdev.devutils.components.NumberField;
import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcore.utils.converters.FunctionalStringConverter;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.util.StringConverter;

import java.util.function.Supplier;

public class FieldSettingComponent<T> extends SettingComponent<T> {
    private final Supplier<FloatingField> fieldFactory;
    private StringConverter<T> converter;

    //================================================================================
    // Constructors
    //================================================================================
    protected FieldSettingComponent(SettingDescriptor<T> descriptor, Supplier<FloatingField> fieldFactory) {
        super(descriptor);
        this.fieldFactory = fieldFactory;
    }

    public static FieldSettingComponent<String> string(SettingDescriptor<String> descriptor) {
        FieldSettingComponent<String> component = new FieldSettingComponent<>(descriptor, FloatingField::new);
        component.setConverter(FunctionalStringConverter.converter(
            s -> s,
            s -> s
        ));
        return component;
    }

    public static FieldSettingComponent<Double> forDouble(SettingDescriptor<Double> descriptor) {
        FieldSettingComponent<Double> component = number(descriptor);
        component.setConverter(FunctionalStringConverter.converter(
            s -> {
                try {return Double.parseDouble(s);} catch (Exception ex) {return descriptor.setting().defValue();}
            },
            String::valueOf
        ));
        return component;
    }

    static <N extends Number> FieldSettingComponent<N> number(SettingDescriptor<N> descriptor) {
        return new FieldSettingComponent<>(descriptor, NumberField::new);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new Skin<>(this);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected static class Skin<T> extends SkinBase<T> {
        private final FloatingField field;

        public Skin(FieldSettingComponent<T> component) {
            super(component);

            field = component.getFieldFactory().get();
            field.setFloatingText(descriptor().description());
            setText(descriptor().setting().get());

            getChildren().add(field);
        }

        protected void setText(T val) {
            FieldSettingComponent<T> component = component();
            field.field().setText(component.getConverter().toString(val));
        }

        protected void setSetting(String val) {
            FieldSettingComponent<T> component = component();
            descriptor().setting().set(component.getConverter().fromString(val));
        }

        @Override
        protected void initBehavior(SettingComponentBehavior<T> behavior) {
            super.initBehavior(behavior);
            register(field.focusWithinProperty(), (ob, o, n) -> {
                if (!n) {
                    String text = field.field().getText();
                    boolean avoidEmpty = descriptor().avoidEmpty();
                    if (text.isBlank() && avoidEmpty) {
                        descriptor().setting().reset();
                        return;
                    }
                    setSetting(text);
                }
            });
        }

        @Override
        protected void settingChanged() {
            super.settingChanged();
            setText(descriptor().setting().get());
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            layoutInArea(
                field,
                x, y, w, h, 0,
                HPos.LEFT, VPos.CENTER
            );
        }

        protected FieldSettingComponent<T> component() {
            return (FieldSettingComponent<T>) getSkinnable();
        }
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Supplier<FloatingField> getFieldFactory() {
        return fieldFactory;
    }

    public StringConverter<T> getConverter() {
        return converter;
    }

    public void setConverter(StringConverter<T> converter) {
        this.converter = converter;
    }
}
