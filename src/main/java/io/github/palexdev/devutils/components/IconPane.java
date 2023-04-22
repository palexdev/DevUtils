package io.github.palexdev.devutils.components;

import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxresources.base.properties.WrappedIconProperty;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class IconPane extends StackPane {
    //================================================================================
    // Properties
    //================================================================================
    private final WrappedIconProperty icon = new WrappedIconProperty();
    private final NodeProperty content = new NodeProperty();

    //================================================================================
    // Constructors
    //================================================================================
    public IconPane() {
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        ObservableList<Node> children = super.getChildren();
        icon.addListener((ov, o, n) -> {
            if (o != null) children.remove(o);
            if (n != null) {
                StackPane.setAlignment(n, Pos.TOP_RIGHT);
                children.add(getChildren().size(), n);
            }
        });
        content.addListener((ov, o, n) -> {
            if (o != null) children.remove(o);
            if (n != null) children.add(0, n);
        });
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildrenUnmodifiable();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public MFXIconWrapper getIcon() {
        return icon.get();
    }

    public WrappedIconProperty iconProperty() {
        return icon;
    }

    public void setIcon(MFXIconWrapper icon) {
        this.icon.set(icon);
    }

    public Node getContent() {
        return content.get();
    }

    public NodeProperty contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }
}
