package io.github.palexdev.devutils.views;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.devutils.App;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.utils.StageUtils;
import io.github.palexdev.devutils.views.settings.AppSettingsView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.mfxcomponents.controls.fab.MFXFab;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxeffects.animations.Animations.KeyFrames;
import io.github.palexdev.mfxeffects.animations.Animations.TimelineBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.animation.Interpolator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

import java.util.Map;

public class MainView extends View {
    //================================================================================
    // Singleton
    //================================================================================
    private static final MainView instance = new MainView();

    public static MainView get() {
        return instance;
    }

    //================================================================================
    // Properties
    //================================================================================
    private final ObjectProperty<View> view = new SimpleObjectProperty<>();
    private MainViewPane root;

    //================================================================================
    // Constructors
    //================================================================================
    private MainView() {
        build();
        Size ws = StageUtils.getWindowSizes();
        Scene scene = new Scene(root, ws.getWidth(), ws.getHeight());
        scene.setFill(Color.TRANSPARENT);
        MFXThemeManager.PURPLE_LIGHT.addOn(scene);

        Stage window = App.window();
        window.setScene(scene);
        window.initStyle(StageStyle.TRANSPARENT);
        window.titleProperty().bind(App.titleProperty());
        setView(DashboardView.get());

        if (Boolean.parseBoolean(System.getenv("DEBUG"))) ScenicView.show(scene);
        CSSFX.start(scene);
    }

    //================================================================================
    // Methods
    //================================================================================
    public void dashboard() {
        setView(DashboardView.get());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void build() {
        root = new MainViewPane();
    }

    @Override
    public Region toRegion() {
        return root;
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public View getView() {
        return view.get();
    }

    public ObjectProperty<View> viewProperty() {
        return view;
    }

    public void setView(View view) {
        this.view.set(view);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    private class MainViewPane extends StackPane {
        private final StackPane content;

        private Node lastSideActive;
        private Map<Class<? extends View>, Node> sideButtonsMap;

        public static final PseudoClass ENABLED = PseudoClass.getPseudoClass("enabled");

        public MainViewPane() {
            Node header = buildHeader();
            Node sidebar = buildSidebar();
            content = new StackPane();
            content.setId("content");
            MFXScrollPane sp = new MFXScrollPane(content);
            sp.setFitToWidth(true);
            sp.setFitToHeight(true);
            BorderPane container = new BorderPane();
            container.setTop(header);
            container.setCenter(sp);
            container.setLeft(sidebar);
            container.getStyleClass().add("main-view");
            getStyleClass().add("stack");
            getStylesheets().add(Resources.loadCss("MainView.css"));
            getChildren().add(container);
            StageUtils.makeResizable(App.window(), container);

            When.onChanged(viewProperty())
                .then((o, v) -> switchTo(v.toRegion()))
                .listen();
        }

        public void switchTo(Region region) {
            if (content.getChildren().isEmpty()) {
                content.getChildren().add(region);
                return;
            }

            Duration d = M3Motion.MEDIUM4;
            Interpolator curve = M3Motion.EMPHASIZED;
            region.setOpacity(0.0);
            content.getChildren().add(0, region);
            Node old = content.getChildren().get(content.getChildren().size() - 1);
            TimelineBuilder.build()
                .add(KeyFrames.of(d, old.opacityProperty(), 0.0, curve))
                .add(KeyFrames.of(d, region.opacityProperty(), 1.0, curve))
                .setOnFinished(e -> content.getChildren().remove(old))
                .getAnimation()
                .play();
        }

        protected Node buildHeader() {
            GridPane header = new GridPane();
            header.getStyleClass().add("header");

            Label title = new Label();
            title.getStyleClass().add("title");
            title.textProperty().bind(App.titleProperty());

            MFXIconWrapper aot = createHeaderIcon("aot");
            MFXIconWrapper minimize = createHeaderIcon("minimize");
            MFXIconWrapper maximize = createHeaderIcon("maximize");
            MFXIconWrapper close = createHeaderIcon("close");

            Stage stage = App.window();
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
                if (e.getButton() == MouseButton.PRIMARY) App.exit();
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
            dash.setOnAction(e -> setView(DashboardView.get()));

            MFXFab settings = new MFXFab(new MFXFontIcon("fas-gear"))
                .addVariants(FABVariants.SMALL);
            settings.setOnAction(e -> setView(AppSettingsView.get()));

            sideButtonsMap = Map.of(
                DashboardView.class, dash,
                AppSettingsView.class, settings
            );
            When.onChanged(viewProperty())
                .then((o, n) -> handleSidePseudo())
                .executeNow()
                .listen();

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

        private void handleSidePseudo() {
            if (lastSideActive != null) {
                lastSideActive.pseudoClassStateChanged(ENABLED, false);
            }
            View view = getView();
            if (view == null) return;

            Class<? extends View> c = view.getClass();
            Node node = sideButtonsMap.get(c);
            if (node == null) return;

            node.pseudoClassStateChanged(ENABLED, true);
            lastSideActive = node;
        }
    }
}
