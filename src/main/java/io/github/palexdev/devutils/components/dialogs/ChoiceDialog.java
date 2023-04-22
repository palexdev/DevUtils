package io.github.palexdev.devutils.components.dialogs;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.devutils.Resources;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.css.themes.Stylesheets;
import io.github.palexdev.materialfx.css.themes.Theme;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcore.base.bindings.MFXBindings;
import io.github.palexdev.mfxcore.base.bindings.Source;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Collection;

public class ChoiceDialog<T> extends MFXGenericDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty description = new SimpleStringProperty("");
    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final ObjectProperty<T> selection = new SimpleObjectProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public ChoiceDialog() {
        super();

        getStylesheets().setAll(
            MFXThemeManager.PURPLE_LIGHT.load(),
            Stylesheets.COMBO_BOX.loadTheme(),
            Stylesheets.COMBO_BOX_CELL.loadTheme(),
            Stylesheets.CONTEXT_MENU.loadTheme(),
            Stylesheets.CONTEXT_MENU_ITEM.loadTheme(),
            Resources.loadComponentCss("ChoiceDialog.css")
        );
        build();
        CSSFX.start(this);
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void build() {
        Label desc = new Label();
        desc.textProperty().bind(description);
        desc.getStyleClass().add("description");

        MFXComboBox<T> combo = new MFXComboBox<>();
        combo.setItems(items);
        combo.setFloatingText(getContentText());
        combo.selectFirst();

        // Bypass read-only with custom bindings
        MFXBindings bindings = MFXBindings.instance();
        bindings.bindBidirectional(selection)
            .addSource(Source.of(combo.selectedItemProperty())
                .implicit(selection)
                .setSourceUpdater((o, n) -> {
                    if (combo.getItems().isEmpty()) return;
                    combo.selectItem(n);
                })
            )
            .addTargetInvalidatingSource(combo.selectedItemProperty())
            .get();

        VBox box = new VBox(desc, combo);
        box.getStyleClass().add("container");
        setContent(box);

        MFXButton confirm = new MFXFilledButton("OK");
        confirm.setOnAction(e -> getScene().getWindow().hide());
        addActions(confirm);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public Theme getTheme() {
        return null;
    }


    //================================================================================
    // Getters/Setters
    //================================================================================

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public ObservableList<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items.setAll(items);
    }

    @SafeVarargs
    public final void setItems(T... items) {
        this.items.setAll(items);
    }

    public T getSelection() {
        return selection.get();
    }

    public ObjectProperty<T> selectionProperty() {
        return selection;
    }

    public void setSelection(T item) {
        selection.set(item);
    }
}
