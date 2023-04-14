package io.github.palexdev.devutils.components;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.mfxcore.utils.fx.TextUtils;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.animation.Interpolator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Supplier;

import static io.github.palexdev.devutils.components.FloatingField.FloatingFieldBehavior;

public class FloatingField extends MFXControl<FloatingFieldBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    private final TextField delegate;
    private final StringProperty floatingText = new SimpleStringProperty("");

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty editable = new StyleableBooleanProperty(
        StyleableProperties.EDITABLE,
        this,
        "editable",
        true
    );

    private final StyleableBooleanProperty selectable = new StyleableBooleanProperty(
        StyleableProperties.SELECTABLE,
        this,
        "selectable",
        true
    );

    //================================================================================
    // Constructors
    //================================================================================
    public FloatingField() {
        this("");
    }

    public FloatingField(String text) {
        this(text, "");
    }

    public FloatingField(String text, String floatingText) {
        delegate = new TextField(text);
        setFloatingText(floatingText);
        initialize();
    }

    public static FloatingField asLabel(String text, String floatingText) {
        FloatingField field = new FloatingField(text, floatingText);
        field.setEditable(false);
        return field;
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        Bindings.bindContent(delegate.getStyleClass(), getStyleClass());
        getStyleClass().setAll(defaultStyleClasses());
        getStylesheets().add(Resources.loadCss("FloatingField.css"));
        setDefaultBehaviorProvider();
        delegate.editableProperty().bind(editableProperty());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new FloatingFieldSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("floating-field");
    }

    @Override
    public Supplier<FloatingFieldBehavior> defaultBehaviorProvider() {
        return () -> new FloatingFieldBehavior(this);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    protected static class StyleableProperties {
        private static final StyleablePropertyFactory<FloatingField> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<FloatingField, Boolean> EDITABLE =
            FACTORY.createBooleanCssMetaData(
                "-fx-editable",
                FloatingField::editableProperty,
                true
            );

        private static final CssMetaData<FloatingField, Boolean> SELECTABLE =
            FACTORY.createBooleanCssMetaData(
                "-fx-selectable",
                FloatingField::selectableProperty,
                true
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXControl.getClassCssMetaData(),
                EDITABLE, SELECTABLE
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public TextField field() {
        return delegate;
    }

    public String getFloatingText() {
        return floatingText.get();
    }

    public StringProperty floatingTextProperty() {
        return floatingText;
    }

    public void setFloatingText(String floatingText) {
        this.floatingText.set(floatingText);
    }

    public boolean isEditable() {
        return editable.get();
    }

    public StyleableBooleanProperty editableProperty() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    public boolean isSelectable() {
        return selectable.get();
    }

    public StyleableBooleanProperty selectableProperty() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable.set(selectable);
    }

    //================================================================================
    // InternalClasses
    //================================================================================
    public static class FloatingFieldBehavior extends BehaviorBase<FloatingField> {
        public FloatingFieldBehavior(FloatingField field) {
            super(field);
        }
    }

    public static class FloatingFieldSkin extends MFXSkinBase<FloatingField, FloatingFieldBehavior> {
        private final VBox container;
        private final Label floating;
        private final TextField delegate;
        private boolean wasEmpty = false;

        private double cachedH = -1;
        private Double cachedY = null;
        private boolean isFloating = false;

        private When<Boolean> focusWhen;
        private When<?> selectionWhen;

        protected static PseudoClass CENTER = PseudoClass.getPseudoClass("center");

        protected FloatingFieldSkin(FloatingField field) {
            super(field);
            delegate = field.field();
            floating = new Label();
            floating.textProperty().bind(field.floatingTextProperty());
            container = new VBox(floating, delegate) {
                @Override
                protected void layoutChildren() {
                    containerLayout(getLayoutX(), getLayoutY(), getWidth(), getHeight());
                }
            };
            container.getStyleClass().add("layout");
            getChildren().add(container);

            focusWhen = When.onInvalidated(field.focusWithinProperty())
                .then(v -> container.requestLayout())
                .executeNow()
                .listen();
            selectionWhen = When.onInvalidated(delegate.selectionProperty())
                .condition(v -> !field.isSelectable())
                .then(v -> delegate.deselect())
                .executeNow(() -> !field.isSelectable())
                .listen();
        }

        protected void animateLabel() {
            FloatingField field = getSkinnable();
            String text = delegate.getText();
            double targetY = 0.0;
            if (text.isEmpty() && !field.isFocusWithin()) {
                wasEmpty = true;
                isFloating = true;
                floating.pseudoClassStateChanged(CENTER, true);

                if (cachedY == null) {
                    cachedY = LayoutUtils.computeYPosition(
                        container, delegate,
                        container.getLayoutY(), container.getHeight(), 0,
                        container.getPadding(), true,
                        VPos.CENTER, true, true
                    );
                } else {
                    System.out.println("Computation Avoided");
                }

                targetY = (delegate.getLayoutY() - cachedY);
            } else if (wasEmpty) {
                wasEmpty = false;
                isFloating = false;
                floating.pseudoClassStateChanged(CENTER, false);
            } else {
                return;
            }

            if (floating.getTranslateY() == targetY) return;
            Duration d = M3Motion.MEDIUM2;
            Interpolator curve = M3Motion.STANDARD;
            TimelineBuilder.build()
                .add(KeyFrames.of(d, floating.translateYProperty(), targetY, curve))
                .getAnimation()
                .play();
            System.out.println("Animating");
        }

        @Override
        protected void initBehavior(FloatingFieldBehavior behavior) {
            FloatingField field = getSkinnable();
            behavior.handler(field, MouseEvent.MOUSE_PRESSED, e -> delegate.requestFocus());
        }

        @Override
        public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return snapSizeY(container.snappedTopInset() +
                TextUtils.computeTextHeight(delegate.getFont(), floating.getText()) +
                container.getSpacing() +
                TextUtils.computeTextHeight(delegate.getFont(), delegate.getText()) +
                container.snappedBottomInset());
        }

        @Override
        public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().prefWidth(height);
        }

        @Override
        public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().prefHeight(width);
        }

        protected void containerLayout(double x, double y, double w, double h) {
            floating.autosize();
            Position floatingPos = LayoutUtils.computePosition(
                container, floating,
                x, y, w, h, 0, container.getPadding(),
                HPos.LEFT, VPos.TOP, true, true
            );
            floating.relocate(floatingPos.getX(), floatingPos.getY());

            double gap = container.getSpacing();
            Position delegatePos = LayoutUtils.computePosition(
                container, delegate,
                x, y, w, h, 0, container.getPadding(),
                HPos.LEFT, VPos.BOTTOM, true, true
            );
            delegate.autosize();
            delegate.relocate(delegatePos.getX(), delegatePos.getY() + gap);

            /*
             * There are three important things to note here!
             * 1) The height cache for when the label is floating is built here.
             * This is important because we have to keep in mind the label may change its sizes (CSS for example).
             * 2) The listener responsible for animating the label when the field acquires focus, triggers this layout
             * method rather than directly animating it. There are at least two benefits I can think of: sizes will always be
             * correct since they are updated above, because of that and for the sake of the above point the cache is also
             * always correct
             * 3) This is also responsible for animating the label. Now, you may think this is expensive in terms of
             * performance, but not so much. You see, the animation is run only when the label must be placed at the center,
             * and even if the layout method is invoked multiple times, the animation will start only if the target position
             * has not been reached yet. These optimizations are inside the animation method and are very important.
             */
            if (isFloating && floating.getHeight() != cachedH) {
                cachedH = floating.getHeight();
                cachedY = null;
            }
            animateLabel();
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            container.resizeRelocate(x, y, w, h);
        }

        @Override
        public void dispose() {
            focusWhen.dispose();
            focusWhen = null;
            selectionWhen.dispose();
            selectionWhen = null;
            super.dispose();
        }
    }
}
