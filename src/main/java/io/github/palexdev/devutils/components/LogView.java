package io.github.palexdev.devutils.components;

import io.github.palexdev.devutils.Resources;
import io.github.palexdev.devutils.beans.Log;
import io.github.palexdev.devutils.enums.LogType;
import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXSkinBase;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import io.github.palexdev.virtualizedfx.cell.Cell;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.flow.VirtualFlow;
import io.github.palexdev.virtualizedfx.utils.VSPUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LogView extends MFXControl<BehaviorBase<LogView>> {
    //================================================================================
    // Properties
    //================================================================================
    private final ObservableList<Log> logs = FXCollections.observableArrayList();

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty autoScroll = new StyleableBooleanProperty(
        StyleableProperties.AUTO_SCROLL,
        this,
        "autoScroll",
        true
    );

    //================================================================================
    // Constructors
    //================================================================================
    public LogView() {
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add("log-view");
        getStylesheets().add(Resources.loadComponentCss("LogView.css"));
    }

    public LogView log(LogType type, String message) {
        logs.add(Log.of(type, message));
        return this;
    }

    public LogView generic(String message) {
        return log(LogType.GENERIC, message);
    }

    public LogView error(String message) {
        return log(LogType.ERROR, message);
    }

    public LogView warn(String message) {
        return log(LogType.WARN, message);
    }

    public LogView ok(String message) {
        return log(LogType.OK, message);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected MFXSkinBase<?, ?> buildSkin() {
        return new LogViewSkin(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("log-view");
    }

    @Override
    public Supplier<BehaviorBase<LogView>> defaultBehaviorProvider() {
        return () -> new BehaviorBase<>(this) {};
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<LogView> FACTORY = new StyleablePropertyFactory<>(MFXControl.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<LogView, Boolean> AUTO_SCROLL =
            FACTORY.createBooleanCssMetaData(
                "-fx-auto-scroll",
                LogView::autoScrollProperty,
                false
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                MFXControl.getClassCssMetaData(),
                AUTO_SCROLL
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
    // Internal Classes
    //================================================================================
    public static class LogViewSkin extends MFXSkinBase<LogView, BehaviorBase<LogView>> {
        private final VirtualScrollPane vsp;
        private final VirtualFlow<Log, LogCell> flow;
        private InvalidationListener scrollListener;
        private When<?> speedWhen;

        protected LogViewSkin(LogView log) {
            super(log);

            flow = new VirtualFlow<>(
                log.getLogs(),
                LogCell::new
            );
            vsp = flow.wrap();

            Consumer<Double> setSpeed = v -> VSPUtils.setVSpeed(vsp, v / 2, v, v * 1.5);
            speedWhen = When.onInvalidated(flow.cellSizeProperty())
                .then(n -> setSpeed.accept(n.doubleValue()))
                .executeNow()
                .listen();

            scrollListener = i -> flow.scrollToLast();
            flow.estimatedLengthProperty().addListener(scrollListener);

            getChildren().add(vsp);
        }

        @Override
        protected void initBehavior(BehaviorBase<LogView> behavior) {
        }

        @Override
        public void dispose() {
            LogView view = getSkinnable();
            view.getLogs().removeListener(scrollListener);
            scrollListener = null;
            speedWhen.dispose();
            speedWhen = null;
            super.dispose();
        }
    }

    public static class LogCell extends Label implements Cell<Log> {
        private final ReadOnlyObjectWrapper<Log> log = new ReadOnlyObjectWrapper<>() {
            @Override
            protected void invalidated() {
                Log log = get();
                setText(log.toString());
                handleStyle();
            }
        };
        public static final PseudoClass ERROR = PseudoClass.getPseudoClass("error");
        public static final PseudoClass WARN = PseudoClass.getPseudoClass("warn");
        public static final PseudoClass OK = PseudoClass.getPseudoClass("ok");

        public LogCell(Log log) {
            updateItem(log);
            getStyleClass().setAll("log-cell");
        }

        protected void handleStyle() {
            LogType type = getLog().type();
            switch (type) {
                case ERROR -> {
                    pseudoClassStateChanged(OK, false);
                    pseudoClassStateChanged(WARN, false);
                    pseudoClassStateChanged(ERROR, true);
                }
                case WARN -> {
                    pseudoClassStateChanged(OK, false);
                    pseudoClassStateChanged(ERROR, false);
                    pseudoClassStateChanged(WARN, true);
                }
                case OK -> {
                    pseudoClassStateChanged(ERROR, false);
                    pseudoClassStateChanged(WARN, false);
                    pseudoClassStateChanged(OK, true);
                }
                default -> {
                    pseudoClassStateChanged(ERROR, false);
                    pseudoClassStateChanged(WARN, false);
                    pseudoClassStateChanged(OK, false);
                }
            }
        }

        @Override
        public Node getNode() {
            return this;
        }

        @Override
        public void updateItem(Log item) {
            setLog(item);
        }

        public Log getLog() {
            return log.get();
        }

        public ReadOnlyObjectProperty<Log> logProperty() {
            return log.getReadOnlyProperty();
        }

        public void setLog(Log log) {
            this.log.set(log);
        }
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public ObservableList<Log> getLogs() {
        return logs;
    }

    public boolean isAutoScroll() {
        return autoScroll.get();
    }

    public StyleableBooleanProperty autoScrollProperty() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll.set(autoScroll);
    }
}
