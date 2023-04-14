package io.github.palexdev.devutils.components;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.layout.LayoutStrategy;
import io.github.palexdev.mfxcomponents.skins.base.MFXLabeledSkin;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.controls.BoundLabel;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import io.github.palexdev.mfxcore.utils.fx.TextUtils;
import io.github.palexdev.mfxresources.base.properties.IconProperty;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import io.github.palexdev.mfxresources.fonts.fontawesome.FontAwesomeSolid;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Supplier;

import static io.github.palexdev.devutils.components.ServiceCard.ServiceCardBehavior;

public class ServiceCard extends MFXLabeled<ServiceCardBehavior> {
    //================================================================================
    // Properties
    //================================================================================
    private Runnable action = () -> {
    };
    private Runnable settingsAction = () -> {
    };
    private final StringProperty description = new SimpleStringProperty("");
    private final IconProperty icon = new IconProperty(new MFXFontIcon());

    //================================================================================
    // Constructors
    //================================================================================
    public ServiceCard() {
        initialize();
    }

    public ServiceCard(String text) {
        this();
        setText(text);
    }

    public ServiceCard(String text, MFXFontIcon icon) {
        this();
        setText(text);
        setIcon(icon);
    }


    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        getStylesheets().add(Resources.loadCss("ServiceCard.css"));
        graphicProperty().bind(icon);
        setDefaultBehaviorProvider();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new ServiceCardSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("service-card");
    }

    @Override
    public Supplier<ServiceCardBehavior> defaultBehaviorProvider() {
        return () -> new ServiceCardBehavior(this);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Runnable getAction() {
        return action;
    }

    public ServiceCard setAction(Runnable action) {
        this.action = action;
        return this;
    }

    public Runnable getSettingsAction() {
        return settingsAction;
    }

    public ServiceCard setSettingsAction(Runnable settingsAction) {
        this.settingsAction = settingsAction;
        return this;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ServiceCard setDescription(String description) {
        this.description.set(description);
        return this;
    }

    public MFXFontIcon getIcon() {
        return icon.get();
    }

    public IconProperty iconProperty() {
        return icon;
    }

    public ServiceCard setIcon(MFXFontIcon icon) {
        this.icon.set(icon);
        return this;
    }

    public ServiceCard setIcon(String description) {
        icon.setDescription(description);
        return this;
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public static class ServiceCardBehavior extends BehaviorBase<ServiceCard> {

        public ServiceCardBehavior(ServiceCard node) {
            super(node);
        }
    }

    public static class ServiceCardSkin extends MFXLabeledSkin<ServiceCard, ServiceCardBehavior> {
        private final MFXIconWrapper icon;
        private final MFXButton actionButton;
        private final MFXButton infoButton;
        private final MFXButton settingsButton;

        private final double ICON_LABEL_GAP = 15;
        private final double LABEL_BUTTONS_GAP = 30;
        private final double BUTTONS_GAP = 15;

        protected When<String> descWhen;

        public ServiceCardSkin(ServiceCard card) {
            super(card);

            icon = new MFXIconWrapper();
            icon.iconProperty().bind(card.iconProperty());
            actionButton = MFXButton.filled();
            actionButton.setText("Open");
            settingsButton = MFXButton.outlined();
            settingsButton.setGraphic(new MFXFontIcon(FontAwesomeSolid.GEAR));
            settingsButton.setLayoutStrategy(settingsButton.getLayoutStrategy()
                .setPrefWidthFunction(LayoutStrategy.Defaults.DEF_PREF_WIDTH_FUNCTION)
            );
            settingsButton.getStyleClass().add("secondary-button");
            infoButton = MFXButton.outlined();
            infoButton.setGraphic(new MFXFontIcon(FontAwesomeSolid.CIRCLE_INFO));
            infoButton.setLayoutStrategy(infoButton.getLayoutStrategy()
                .setPrefWidthFunction(LayoutStrategy.Defaults.DEF_PREF_WIDTH_FUNCTION)
            );
            infoButton.getStyleClass().add("secondary-button");
            infoButton.hoverProperty().addListener(i ->
                infoButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false)
            );

            MFXTooltip infoTooltip = new MFXTooltip(infoButton);
            infoTooltip.setShowDelay(Duration.millis(250));
            descWhen = When.onChanged(card.descriptionProperty())
                .then((o, n) -> {
                    infoTooltip.setText(n);
                    if (!n.isBlank()) {
                        infoTooltip.install();
                    } else {
                        infoTooltip.uninstall();
                        infoTooltip.hide();
                    }
                })
                .executeNow()
                .listen();

            getChildren().addAll(icon, label, actionButton, infoButton, settingsButton);
        }

        @Override
        protected void initBehavior(ServiceCardBehavior behavior) {
            ServiceCard card = getSkinnable();
            behavior.handler(actionButton, ActionEvent.ACTION, e -> card.getAction().run());
            behavior.handler(settingsButton, ActionEvent.ACTION, e -> card.getSettingsAction().run());
        }

        @Override
        protected BoundLabel createLabel(ServiceCard labeled) {
            BoundLabel label = super.createLabel(labeled);
            label.graphicProperty().unbind();
            label.setGraphic(null);
            return label;
        }

        @Override
        public double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            ServiceCard card = getSkinnable();
            double iW = icon.getSize();
            double textW = TextUtils.computeTextWidth(card.getFont(), card.getText());
            double buttonsW = LayoutUtils.boundWidth(actionButton) +
                BUTTONS_GAP +
                LayoutUtils.boundWidth(settingsButton) +
                BUTTONS_GAP +
                LayoutUtils.boundWidth(infoButton);
            return leftInset + Math.max(textW, Math.max(iW, buttonsW)) + rightInset;
        }

        @Override
        public double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return topInset +
                icon.getSize() +
                ICON_LABEL_GAP +
                LayoutUtils.boundHeight(label) +
                LABEL_BUTTONS_GAP +
                Math.max(
                    Math.max(
                        LayoutUtils.boundHeight(actionButton),
                        LayoutUtils.boundHeight(settingsButton)
                    ),
                    LayoutUtils.boundHeight(infoButton)
                ) +
                bottomInset;
        }

        @Override
        public double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().prefWidth(height);
        }

        @Override
        public double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return getSkinnable().prefHeight(width);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            double yOffset = 0;
            double iSize = icon.getSize();
            icon.resize(iSize, iSize);
            positionInArea(icon, x, y, w, h, 0, HPos.CENTER, VPos.TOP);
            yOffset += icon.getHeight() + ICON_LABEL_GAP;

            layoutInArea(label, x, y + yOffset, w, h, 0, HPos.CENTER, VPos.TOP);
            yOffset += label.getHeight() + LABEL_BUTTONS_GAP;

            double abw = LayoutUtils.boundWidth(actionButton);
            double sbw = LayoutUtils.boundWidth(settingsButton);
            double ibw = LayoutUtils.boundWidth(infoButton);
            double actionsWidth = abw + BUTTONS_GAP + sbw + BUTTONS_GAP + ibw;

            double actionsCenter = (w + snappedLeftInset() + snappedRightInset() - actionsWidth) / 2;
            double aY = y + yOffset;

            actionButton.autosize();
            actionButton.relocate(actionsCenter, aY);
            infoButton.autosize();
            infoButton.relocate(actionButton.getLayoutX() + abw + BUTTONS_GAP, aY);
            settingsButton.autosize();
            settingsButton.relocate(infoButton.getLayoutX() + ibw + BUTTONS_GAP, aY);
        }

        @Override
        public void dispose() {
            descWhen.dispose();
            super.dispose();
        }
    }
}
