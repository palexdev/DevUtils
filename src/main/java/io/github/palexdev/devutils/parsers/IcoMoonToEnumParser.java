package io.github.palexdev.devutils.parsers;

import io.github.palexdev.devutils.parsers.base.FileParserBase;
import io.github.palexdev.devutils.settings.IcoMoonToEnumSettings;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IcoMoonToEnumParser extends FileParserBase<String> {
    //================================================================================
    // Properties
    //================================================================================
    private final IcoMoonToEnumSettings settings;

    //================================================================================
    // Constructors
    //================================================================================
    public IcoMoonToEnumParser(IcoMoonToEnumSettings settings) {
        this.settings = settings;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public String parse() {
        String prefix = settings.prefix.get();
        StringBuilder sb = new StringBuilder();
        List<String> filtered = read().stream()
            .filter(s -> s.contains("glyph-name"))
            .toList();

        AtomicInteger cnt = new AtomicInteger(0);
        filtered.forEach(l -> {
            String[] parsed = parseLine(l);
            String name = parsed[0].replace("-", "_")
                .replace("\"", "")
                .toUpperCase();

            // Name cannot start with a number
            if (Character.isDigit(name.charAt(0)))
                name = "_" + name;

            String desc = parsed[0]
                .replace("\"", "")
                .toLowerCase();
            String code = "'\\u" + parsed[1].toUpperCase() + "'";
            String constant = "%s(\"%s\", %s),\n".formatted(name, prefix + desc, code);
            sb.append(constant);
            onLineParsed("Parsed icon: %s".formatted(name));
            cnt.incrementAndGet();
        });
        sb.append(";");
        onParsed("Parsed %d icons".formatted(cnt.get()));
        return sb.toString();
    }

    @Override
    public boolean isFileValid() {
        if (file == null) return false;
        List<String> lines = read();
        return lines.parallelStream().anyMatch(s -> s.contains("metadata") || s.contains("Generated by IcoMoon"));
    }

    //================================================================================
    // Methods
    //================================================================================
    protected String[] parseLine(String line) {
        String[] tokens = line.replace("<glyph ", "")
            .replace(" />", "")
            .trim()
            .split(" ");
        String code = tokens[0].split("=")[1]
            .replace("\"", "")
            .replace("&#x", "")
            .replace(";", "");
        String name = tokens[1].split("=")[1];
        return new String[]{name, code};
    }
}
