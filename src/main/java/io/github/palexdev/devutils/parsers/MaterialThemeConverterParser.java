package io.github.palexdev.devutils.parsers;

import io.github.palexdev.devutils.enums.MaterialThemeColor;
import io.github.palexdev.devutils.enums.MaterialThemeScheme;
import io.github.palexdev.devutils.enums.MaterialThemeTokens;
import io.github.palexdev.devutils.enums.MaterialThemeTypeface;
import io.github.palexdev.devutils.parsers.base.FileParserBase;
import io.github.palexdev.devutils.settings.MaterialThemeConverterSettings;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.palexdev.devutils.enums.MaterialThemeColor.*;
import static io.github.palexdev.devutils.enums.MaterialThemeScheme.DARK;
import static io.github.palexdev.devutils.enums.MaterialThemeScheme.LIGHT;
import static io.github.palexdev.devutils.enums.MaterialThemeTokens.*;
import static io.github.palexdev.devutils.enums.MaterialThemeTypeface.*;
import static java.util.stream.Collectors.groupingBy;

@Component
public class MaterialThemeConverterParser extends FileParserBase<String> {
    //================================================================================
    // Properties
    //================================================================================
    private final MaterialThemeConverterSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    public MaterialThemeConverterParser(MaterialThemeConverterSettings settings) {
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public boolean isFileValid() {
        if (file == null) return false;
        List<String> lines = read();
        return lines.get(0).startsWith(":root");
    }

    @Override
    public String parse() {
        Boolean includeTypefaces = settings.includeTypefaces.get();
        Map<MaterialThemeTokens, List<String>> tokens = read().stream()
            .filter(s -> s.trim().startsWith("--"))
            .collect(groupingBy(this::stringToTokenType));

        String source = getSource(tokens.get(SOURCE));
        String palette = generatePalette(tokens.get(PALETTE));
        String[] schemes = generateSchemes(tokens.get(SCHEME));

        StringBuilder sb = new StringBuilder();
        sb.append("$palette: (\n").append(source).append("\n").append(palette).append(");\n\n");
        sb.append("$light-scheme: (\n").append(schemes[0]).append(");\n\n");
        sb.append("$dark-scheme: (\n").append(schemes[1]).append(");\n\n");

        if (includeTypefaces) {
            List<String> typefaces = generateTypefaces(tokens.get(TYPEFACE));
            for (String typeface : typefaces) {
                sb.append(typeface).append("\n\n");
            }
        }

        return sb.toString();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected String getSource(List<String> lines) {
        String source = lines.get(0); // There's a single source
        return processLine(source);
    }

    protected String generatePalette(List<String> lines) {
        Map<MaterialThemeColor, List<String>> palette = lines.stream()
            .collect(groupingBy(
                    this::stringToColorType,
                    () -> new TreeMap<>(Comparator.comparing(Enum::ordinal)),
                    Collectors.toList()
                )
            );

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<MaterialThemeColor, List<String>> e : palette.entrySet()) {
            String name = e.getKey().toString();
            sb.append("  // ").append(name).append("\n");
            List<String> tokens = e.getValue();
            for (String token : tokens) {
                sb.append(processLine(token)).append("\n");
            }
        }
        return sb.toString();
    }

    protected String[] generateSchemes(List<String> lines) {
        Map<MaterialThemeScheme, List<String>> schemes = lines.stream()
            .collect(groupingBy(
                    this::stringToSchemeType,
                    () -> new TreeMap<>(Comparator.comparing(Enum::ordinal)),
                    Collectors.toList()
                )
            );

        String[] out = new String[2];
        StringBuilder sb = new StringBuilder();

        // Light Scheme
        List<String> light = schemes.get(LIGHT);
        for (String token : light) {
            sb.append(processSchemeLine(LIGHT, token)).append("\n");
        }
        out[0] = sb.toString();

        sb.setLength(0);
        List<String> dark = schemes.get(DARK);
        for (String token : dark) {
            sb.append(processSchemeLine(DARK, token)).append("\n");
        }
        out[1] = sb.toString();

        return out;
    }

    protected List<String> generateTypefaces(List<String> lines) {
        Boolean ignoreUseless = settings.ignoreUselessTokens.get();
        Map<MaterialThemeTypeface, List<String>> typefaces = lines.stream()
            .collect(groupingBy(
                    this::stringToTypeface,
                    () -> new TreeMap<>(Comparator.comparing(Enum::ordinal)),
                    Collectors.toList()
                )
            );
        List<String> out = new ArrayList<>();

        for (Map.Entry<MaterialThemeTypeface, List<String>> e : typefaces.entrySet()) {
            MaterialThemeTypeface type = e.getKey();
            List<String> tokens = e.getValue();

            String font = "";
            String weight = "";
            String size = "";

            // Parse properties
            for (String token : tokens) {
                if (token.contains("-family-name")) {
                    font = token.split(":")[1].trim().replace(";", " ");
                    continue;
                }
                if (token.contains("-family-style")) {
                    font += token.split(":")[1].trim().replace(";", "");
                    continue;
                }
                if (token.contains("-weight")) {
                    weight = token.split(":")[1].trim();
                    continue;
                }
                if (token.contains("-size")) {
                    size = token.split(":")[1].trim();
                    continue;
                }
                if (ignoreUseless) break;
            }

            // Generate function
            String content = """
                  -fx-font-family: "%s";
                  -fx-font-weight: %s
                  -fx-font-size: %s
                """.formatted(font, weight, size);
            String function = type.generateFunction(content);
            out.add(function);
        }
        return out;
    }

    protected String processLine(String l) {
        String[] keyValue = l.split(":");
        return "  '" + keyValue[0].trim() + "': " +
            keyValue[1].trim().replace(";", ",");
    }

    protected String processSchemeLine(MaterialThemeScheme scheme, String l) {
        String tmp = l.replace(scheme.getId() + ":", ":");
        return processLine(tmp);
    }

    protected MaterialThemeTokens stringToTokenType(String l) {
        return switch (l) {
            case String s when s.trim().startsWith(SOURCE.getId()) -> SOURCE;
            case String s when s.trim().startsWith(PALETTE.getId()) -> PALETTE;
            case String s when s.trim().startsWith(SCHEME.getId()) -> SCHEME;
            case String s when s.trim().startsWith(TYPEFACE.getId()) -> TYPEFACE;
            default -> MaterialThemeTokens.UNKNOWN;
        };
    }

    protected MaterialThemeColor stringToColorType(String l) {
        return switch (l) {
            case String s when s.contains(PRIMARY.getId()) -> PRIMARY;
            case String s when s.contains(SECONDARY.getId()) -> SECONDARY;
            case String s when s.contains(TERTIARY.getId()) -> TERTIARY;
            case String s when s.contains(ERROR.getId()) -> ERROR;
            case String s when s.contains(NEUTRAL_VARIANT.getId()) -> NEUTRAL_VARIANT;
            case String s when s.contains(NEUTRAL.getId()) -> NEUTRAL;
            default -> CUSTOM;
        };
    }

    protected MaterialThemeScheme stringToSchemeType(String l) {
        return switch (l) {
            case String s when s.contains(LIGHT.getId()) -> LIGHT;
            case String s when s.contains(DARK.getId()) -> DARK;
            default -> MaterialThemeScheme.UNKNOWN;
        };
    }

    protected MaterialThemeTypeface stringToTypeface(String l) {
        return switch (l) {
            case String s when s.contains(DISPLAY_LARGE.getId()) -> DISPLAY_LARGE;
            case String s when s.contains(DISPLAY_MEDIUM.getId()) -> DISPLAY_MEDIUM;
            case String s when s.contains(DISPLAY_SMALL.getId()) -> DISPLAY_SMALL;
            case String s when s.contains(HEADLINE_LARGE.getId()) -> HEADLINE_LARGE;
            case String s when s.contains(HEADLINE_MEDIUM.getId()) -> HEADLINE_MEDIUM;
            case String s when s.contains(HEADLINE_SMALL.getId()) -> HEADLINE_SMALL;
            case String s when s.contains(BODY_LARGE.getId()) -> BODY_LARGE;
            case String s when s.contains(BODY_MEDIUM.getId()) -> BODY_MEDIUM;
            case String s when s.contains(BODY_SMALL.getId()) -> BODY_SMALL;
            case String s when s.contains(LABEL_LARGE.getId()) -> LABEL_LARGE;
            case String s when s.contains(LABEL_MEDIUM.getId()) -> LABEL_MEDIUM;
            case String s when s.contains(LABEL_SMALL.getId()) -> LABEL_SMALL;
            case String s when s.contains(TITLE_LARGE.getId()) -> TITLE_LARGE;
            case String s when s.contains(TITLE_MEDIUM.getId()) -> TITLE_MEDIUM;
            case String s when s.contains(TITLE_SMALL.getId()) -> TITLE_SMALL;
            default -> MaterialThemeTypeface.UNKNOWN;
        };
    }

    @Override
    public void setFile(File file) {
        super.setFile(file);
    }
}
