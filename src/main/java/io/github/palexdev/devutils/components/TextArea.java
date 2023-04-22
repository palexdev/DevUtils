package io.github.palexdev.devutils.components;

import io.github.palexdev.devutils.Resources;

public class TextArea extends javafx.scene.control.TextArea {
    {
        getStylesheets().add(Resources.loadComponentCss("TextAreaReset.css"));
    }
}
