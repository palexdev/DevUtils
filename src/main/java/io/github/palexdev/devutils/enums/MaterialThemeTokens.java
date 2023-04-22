package io.github.palexdev.devutils.enums;

public enum MaterialThemeTokens {
    SOURCE("--md-source"),
    PALETTE("--md-ref-palette-"),
    SCHEME("--md-sys-color-"),
    TYPEFACE("--md-sys-typescale-"),
    UNKNOWN("")
    ;

    private final String id;

    MaterialThemeTokens(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
