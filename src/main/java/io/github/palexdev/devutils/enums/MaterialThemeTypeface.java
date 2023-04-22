package io.github.palexdev.devutils.enums;

public enum MaterialThemeTypeface {
    DISPLAY_LARGE("-display-large-"),
    DISPLAY_MEDIUM("-display-medium-"),
    DISPLAY_SMALL("-display-small-"),
    HEADLINE_LARGE("-headline-large-"),
    HEADLINE_MEDIUM("-headline-medium-"),
    HEADLINE_SMALL("-headline-small-"),
    BODY_LARGE("-body-large-"),
    BODY_MEDIUM("-body-medium-"),
    BODY_SMALL("-body-small-"),
    LABEL_LARGE("-label-large-"),
    LABEL_MEDIUM("-label-medium-"),
    LABEL_SMALL("-label-small-"),
    TITLE_LARGE("-title-large-"),
    TITLE_MEDIUM("-title-medium-"),
    TITLE_SMALL("-title-small-"),
    UNKNOWN(""),
    ;

    private final String id;

    MaterialThemeTypeface(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String generateFunction(String content) {
        StringBuilder sb = new StringBuilder();

        // Generate function name
        String[] words = name().split("_");
        for (String word : words) {
            sb.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }
        String funName = sb.toString().trim();
        return "@mixin %s() {\n%s}".formatted(funName, content);
    }
}
