package io.github.palexdev.devutils.views;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.events.StageReadyEvent;
import io.github.palexdev.devutils.events.ViewSwitchEvent;
import io.github.palexdev.devutils.settings.AppSettings;
import io.github.palexdev.devutils.utils.StageUtils;
import io.github.palexdev.devutils.views.MainView.MainPane;
import io.github.palexdev.devutils.views.base.View;
import io.github.palexdev.devutils.views.settings.AppSettingsView;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.scenicview.ScenicView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/* TODO Improve navigation by changing 'back' actions to go
 *  to the previous view, just add a method and a field to SpringHelper
 */
@Component
public class MainView extends View<MainPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final Stage stage;
    private final AppSettings settings;

    private View<?> currentView;
    private Animation animation;

    //================================================================================
    // Constructors
    //================================================================================
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    MainView(Stage stage, AppSettings settings) {
        this.stage = stage;
        this.settings = settings;
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void switchView(View<?> view) {
        // Do not switch if still animating switch!
        // May cause exceptions in case of fast clicking
        if (animation != null && Animations.isPlaying(animation)) return;

        Region region = view.toRegion();
        if (currentView == null) {
            currentView = view;
            root.addContent(region);
            root.handleSidePseudo(view);
            return;
        }

        // Do not animate if the current view is already the requested one
        if (currentView == view) return;

        Duration d = M3Motion.MEDIUM4;
        Interpolator curve = M3Motion.STANDARD;
        region.setOpacity(0.0);
        Node old = currentView.toRegion();
        root.addContent(region);
        animation = Animations.TimelineBuilder.build()
            .add(Animations.KeyFrames.of(d, old.opacityProperty(), 0.0, curve))
            .add(Animations.KeyFrames.of(d, region.opacityProperty(), 1.0, curve))
            .setOnFinished(e -> root.removeContent(old))
            .getAnimation();
        animation.play();
        currentView = view;
        root.handleSidePseudo(view);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MainPane build() {
        return new MainPane();
    }

    //================================================================================
    // Events Handling
    //================================================================================
    @Override
    public void onStageReady(StageReadyEvent event) {
        super.onStageReady(event);

        Size ws;
        try {
            double w = settings.windowWidth.get();
            double h = settings.windowHeight.get();
            ws = Size.of(w, h);

        } catch (Exception ignored) {
            ws = Size.of(settings.windowWidth.defValue(), settings.windowHeight.defValue());
        }
        StageUtils.clampWindowSizes(ws);

        Scene scene = new Scene(root, ws.getWidth(), ws.getHeight());
        scene.setFill(Color.TRANSPARENT);
        MFXThemeManager.PURPLE_LIGHT.addOn(scene);

        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("DevUtils");
        stage.show();

        if (settings.isDebug()) ScenicView.show(scene);
        CSSFX.start(scene);
    }

    @EventListener
    public void onViewSwitchRequest(ViewSwitchEvent event) {
        switchView(event.getView());
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    protected class MainPane extends StackPane {
        private final StackPane content;

        private Node lastSideActive;
        private Map<Class<? extends View<?>>, Node> sideButtonsMap;

        public static final PseudoClass ENABLED = PseudoClass.getPseudoClass("enabled");

        public MainPane() {
            Node header = buildHeader();
            Node sidebar = buildSidebar();
            content = new StackPane();
            content.setId("content");
            BorderPane container = new BorderPane();
            container.setTop(header);
            container.setCenter(content);
            container.setLeft(sidebar);
            container.getStyleClass().add("main-view");
            getStyleClass().add("stack");
            getStylesheets().add(Resources.loadViewCss("MainView.css"));
            getChildren().add(container);
            StageUtils.makeResizable(stage, container);
        }

        protected Node buildHeader() {
            GridPane header = new GridPane();
            header.getStyleClass().add("header");

            Label title = new Label();
            title.getStyleClass().add("title");
            title.textProperty().bind(stage.titleProperty());

            MFXIconWrapper aot = createHeaderIcon("aot");
            MFXIconWrapper minimize = createHeaderIcon("minimize");
            MFXIconWrapper maximize = createHeaderIcon("maximize");
            MFXIconWrapper close = createHeaderIcon("close");

            aot.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    stage.setAlwaysOnTop(!stage.isAlwaysOnTop());
                    aot.pseudoClassStateChanged(ENABLED, stage.isAlwaysOnTop());
                }
            });
            minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) stage.setIconified(true);
            });
            maximize.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) stage.setMaximized(!stage.isMaximized());
            });
            close.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.getButton() == MouseButton.PRIMARY) SpringHelper.exit(close);
            });

            header.add(aot, 0, 0);
            header.add(title, 1, 0);
            header.add(minimize, 2, 0);
            header.add(maximize, 3, 0);
            header.add(close, 4, 0);
            for (int i = 0; i < 4; i++) {
                header.getColumnConstraints().add(new ColumnConstraints());
            }
            ColumnConstraints mid = new ColumnConstraints();
            mid.setHgrow(Priority.ALWAYS);
            mid.setHalignment(HPos.CENTER);
            header.getColumnConstraints().add(1, mid);
            StageUtils.makeDraggable(stage, header);
            return header;
        }

        protected Node buildSidebar() {
            MFXFab dash = new MFXFab(new MFXFontIcon("fas-house"))
                .addVariants(FABVariants.SMALL);
            dash.setOnAction(e -> SpringHelper.setView(DashboardView.class));

            MFXFab settings = new MFXFab(new MFXFontIcon("fas-gear"))
                .addVariants(FABVariants.SMALL);
            settings.setOnAction(e -> SpringHelper.setView(AppSettingsView.class));

            sideButtonsMap = Map.of(
                DashboardView.class, dash,
                AppSettingsView.class, settings
            );

            VBox box = new VBox(dash, settings);
            box.getStyleClass().add("sidebar");
            return box;
        }

        private MFXIconWrapper createHeaderIcon(String typeClass) {
            MFXIconWrapper icon = new MFXIconWrapper();
            icon.getStyleClass().addAll("header-icon", typeClass);
            icon.setIcon("fas-circle");
            return icon;
        }

        @SuppressWarnings("rawtypes")
        protected void handleSidePseudo(View<?> view) {
            if (lastSideActive != null) {
                lastSideActive.pseudoClassStateChanged(ENABLED, false);
            }
            if (view == null) return;
            Class<? extends View> c = view.getClass();
            Node node = sideButtonsMap.get(c);
            if (node == null) return;

            node.pseudoClassStateChanged(ENABLED, true);
            lastSideActive = node;
        }

        protected void addContent(Node content) {
            this.content.getChildren().add(content);
        }

        protected void removeContent(Node content) {
            this.content.getChildren().remove(content);
        }

        protected Node getContent() {
            return content.getChildren().get(0);
        }
    }
}
