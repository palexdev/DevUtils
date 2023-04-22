package io.github.palexdev.devutils.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ChangeType {
    IGNORE("Ignore", ""),
    VERSION("Version", ":bookmark:"),
    ADD("Added", ":sparkles:"),
    CHANGE("Changed", ":recycle:"),
    DEPRECATION("Deprecated", ":wastebasket:"),
    REMOVE("Removed", ":fire:"),
    FIX("Fixed", ":bug:"),
    ;

    //================================================================================
    // Static Members
    //================================================================================
    private static Map<String, ChangeType> cache;

    public static Optional<ChangeType> getType(String s) {
        if (cache == null) {
            cache = Arrays.stream(values())
                .collect(Collectors.toMap(
                    c -> c.id,
                    c -> c
                ));
        }
        if (!s.startsWith(":")) return Optional.of(IGNORE);

        // Extract start of line
        Matcher matcher = Pattern.compile("^[:][\\S]+").matcher(s);
        if (!matcher.find()) return Optional.empty();

        String start = matcher.group();
        ChangeType type = cache.getOrDefault(start, null);
        return Optional.ofNullable(type);
    }

    //================================================================================
    // Misc
    //================================================================================
    private final String name;
    private final String id;

    ChangeType(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
