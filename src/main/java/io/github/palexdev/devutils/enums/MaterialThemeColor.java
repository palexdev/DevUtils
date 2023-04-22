package io.github.palexdev.devutils.enums;

public enum MaterialThemeColor {
    PRIMARY("-primary"),
    SECONDARY("-secondary"),
    TERTIARY("-tertiary"),
    ERROR("-error"),
    NEUTRAL("-neutral"),
    NEUTRAL_VARIANT("-neutral-variant"),
    CUSTOM(""),
    ;

    private final String id;

    MaterialThemeColor(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        String[] words = name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word.charAt(0)).append(word.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
