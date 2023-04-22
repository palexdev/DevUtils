package io.github.palexdev.devutils.components.dialogs;

import io.github.palexdev.devutils.SpringHelper;
import io.github.palexdev.devutils.views.base.View;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.mfxcore.observables.When;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

// Fuck Java generics, no wander people hate on Java, I can absolutely relate to them sometimes
@SuppressWarnings({"rawtypes", "unchecked"})
public interface DialogsServiceBase {

    <T> Optional<T> showChoice(Class<? extends View<?>> view, DialogConfig<ChoiceDialog<T>> config);

    String showField(Class<? extends View<?>> view, DialogConfig<FieldDialog> config);

    void showOutput(Class<? extends View<?>> view, DialogConfig<OutputDialog> config);

    void showDialog(Class<? extends View<?>> view, MFXGenericDialog content, DialogConfig config);

    @Component
    class DialogsService implements DialogsServiceBase {
        private final Map<Class<? extends View<?>>, MFXStageDialog> cache = new HashMap<>();

        @Override
        public <T> Optional<T> showChoice(Class<? extends View<?>> view, DialogConfig<ChoiceDialog<T>> config) {
            ChoiceDialog<T> content = new ChoiceDialog<>();
            showDialog(view, content, config);
            return Optional.ofNullable(content.getSelection());
        }

        @Override
        public String showField(Class<? extends View<?>> view, DialogConfig<FieldDialog> config) {
            FieldDialog content = new FieldDialog();
            showDialog(view, content, config);
            return content.getValue();
        }

        @Override
        public void showOutput(Class<? extends View<?>> view, DialogConfig<OutputDialog> config) {
            showDialog(view, new OutputDialog(), config);
        }

        @Override
        public void showDialog(Class<? extends View<?>> view, MFXGenericDialog content, DialogConfig config) {
            MFXStageDialog sd = cache.computeIfAbsent(view, v -> new MFXStageDialog());
            config.configure(sd);

            content.alwaysOnTopProperty().bind(sd.alwaysOnTopProperty());
            content.setOnAlwaysOnTop(event -> sd.setAlwaysOnTop(!content.isAlwaysOnTop()));
            content.setOnMinimize(event -> sd.setIconified(true));
            content.setOnClose(event -> sd.close());
            config.configure(content);

            sd.setContent(content);
            sd.showAndWait();
        }
    }

    class DialogConfig<D extends MFXGenericDialog> {
        private Consumer<D> onConfigure = d -> {
        };
        private String headerText = "";
        private String contentText = "";
        private boolean showAlwaysOnTop = true;
        private boolean showMinimize = true;
        private boolean showClose = true;
        private Modality modality = Modality.NONE;
        private Window owner = null;
        private Pane ownerNode = null;
        private boolean centerInOwnerNode = true;
        private boolean scrimOwner = true;
        private double scrimStrength = 0.2;
        private ScrimPriority scrimPriority = ScrimPriority.WINDOW;
        private boolean draggable = true;

        protected void configure(D content) {
            content.setHeaderText(headerText);
            content.setContentText(contentText);
            content.setShowAlwaysOnTop(showAlwaysOnTop);
            content.setShowMinimize(showMinimize);
            content.setShowClose(showClose);
            onConfigure.accept(content);
        }

        protected void configure(MFXStageDialog sd) {
            try {
                sd.initModality(modality);
                sd.initOwner(owner);
            } catch (Exception ignored) {
            }
            sd.setOwnerNode(ownerNode);
            sd.setCenterInOwnerNode(centerInOwnerNode);
            sd.setScrimOwner(scrimOwner);
            sd.setScrimStrength(scrimStrength);
            sd.setScrimPriority(scrimPriority);
            sd.setDraggable(draggable);
        }

        public DialogConfig<D> implicitOwner() {
            Stage stage = SpringHelper.getBean(Stage.class);
            When.onChanged(stage.showingProperty())
                .condition((o, n) -> n)
                .then((o, n) -> {
                    setOwner(stage);
                    setModality(Modality.WINDOW_MODAL);
                    setOwnerNode((Pane) stage.getScene().getRoot());
                })
                .executeNow(stage::isShowing)
                .oneShot(true)
                .listen();
            return this;
        }

        public Consumer<D> getOnConfigure() {
            return onConfigure;
        }

        public DialogConfig<D> setOnConfigure(Consumer<D> onConfigure) {
            this.onConfigure = onConfigure;
            return this;
        }

        public String getHeaderText() {
            return headerText;
        }

        public DialogConfig<D> setHeaderText(String headerText) {
            this.headerText = headerText;
            return this;
        }

        public String getContentText() {
            return contentText;
        }

        public DialogConfig<D> setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public boolean isShowAlwaysOnTop() {
            return showAlwaysOnTop;
        }

        public DialogConfig<D> setShowAlwaysOnTop(boolean showAlwaysOnTop) {
            this.showAlwaysOnTop = showAlwaysOnTop;
            return this;
        }

        public boolean isShowMinimize() {
            return showMinimize;
        }

        public DialogConfig<D> setShowMinimize(boolean showMinimize) {
            this.showMinimize = showMinimize;
            return this;
        }

        public boolean isShowClose() {
            return showClose;
        }

        public DialogConfig<D> setShowClose(boolean showClose) {
            this.showClose = showClose;
            return this;
        }

        public Modality getModality() {
            return modality;
        }

        public DialogConfig<D> setModality(Modality modality) {
            this.modality = modality;
            return this;
        }

        public Window getOwner() {
            return owner;
        }

        public DialogConfig<D> setOwner(Window owner) {
            this.owner = owner;
            return this;
        }

        public Pane getOwnerNode() {
            return ownerNode;
        }

        public DialogConfig<D> setOwnerNode(Pane ownerNode) {
            this.ownerNode = ownerNode;
            return this;
        }

        public boolean isCenterInOwnerNode() {
            return centerInOwnerNode;
        }

        public DialogConfig<D> setCenterInOwnerNode(boolean centerInOwnerNode) {
            this.centerInOwnerNode = centerInOwnerNode;
            return this;
        }

        public boolean isScrimOwner() {
            return scrimOwner;
        }

        public DialogConfig<D> setScrimOwner(boolean scrimOwner) {
            this.scrimOwner = scrimOwner;
            return this;
        }

        public double getScrimStrength() {
            return scrimStrength;
        }

        public DialogConfig<D> setScrimStrength(double scrimStrength) {
            this.scrimStrength = scrimStrength;
            return this;
        }

        public ScrimPriority getScrimPriority() {
            return scrimPriority;
        }

        public DialogConfig<D> setScrimPriority(ScrimPriority scrimPriority) {
            this.scrimPriority = scrimPriority;
            return this;
        }

        public boolean isDraggable() {
            return draggable;
        }

        public DialogConfig<D> setDraggable(boolean draggable) {
            this.draggable = draggable;
            return this;
        }
    }
}
