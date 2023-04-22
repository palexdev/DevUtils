package io.github.palexdev.devutils.components.settings;

import io.github.palexdev.devutils.settings.base.SettingDescriptor;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import javafx.geometry.HPos;
import javafx.geometry.VPos;

public class BooleanSettingComponent extends SettingComponent<Boolean> {

    //================================================================================
    // Constructors
    //================================================================================
    public BooleanSettingComponent(SettingDescriptor<Boolean> descriptor) {
        super(descriptor);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new Skin(this);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected static class Skin extends SkinBase<Boolean> {
        private final MFXCheckbox check;

        public Skin(BooleanSettingComponent component) {
            super(component);
            check = new MFXCheckbox(descriptor().description());
            check.setSelected(descriptor().setting().get());
            getChildren().add(check);
        }

        @Override
        protected void initBehavior(SettingComponentBehavior<Boolean> behavior) {
            super.initBehavior(behavior);
            behavior.register(check.selectedProperty(), (ob, o, n) -> descriptor().setting().set(n));
        }

        @Override
        protected void settingChanged() {
            super.settingChanged();
            Boolean val = descriptor().setting().get();
            check.setSelected(val);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            super.layoutChildren(x, y, w, h);
            layoutInArea(
                check,
                x, y, w, h, 0,
                HPos.LEFT, VPos.CENTER
            );
        }

        public BooleanSettingComponent component() {
            return (BooleanSettingComponent) getSkinnable();
        }
    }
}
