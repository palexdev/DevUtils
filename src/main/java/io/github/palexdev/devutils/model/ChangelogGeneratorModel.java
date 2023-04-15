package io.github.palexdev.devutils.model;

import io.github.palexdev.devutils.enums.ChangeType;
import io.github.palexdev.devutils.views.ChangelogGeneratorView;
import io.github.palexdev.mfxcore.base.properties.resettable.ResettableStringProperty;
import javafx.beans.property.ReadOnlyStringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.palexdev.devutils.settings.SettingsDB.CHANGELOG_DATE_FORMATTER;

public class ChangelogGeneratorModel implements ServiceModel {
    //================================================================================
    // Properties
    //================================================================================
    private final ResettableStringProperty changelog = new ResettableStringProperty("", "");
    private final ResettableStringProperty version = new ResettableStringProperty("", "");
    private final ResettableStringProperty parsed = new ResettableStringProperty("", "");
    private final ResettableStringProperty currentLine = new ResettableStringProperty("", "");
    private final Parser parser;

    //================================================================================
    // Constructors
    //================================================================================
    public ChangelogGeneratorModel() {
        changelog.setFireChangeOnReset(true);
        version.setFireChangeOnReset(true);
        parsed.setFireChangeOnReset(true);
        currentLine.setFireChangeOnReset(true);
        this.parser = new Parser();
    }

    //================================================================================
    // Methods
    //================================================================================
    protected void reset() {
        changelog.reset();
        version.reset();
        parsed.reset();
        currentLine.reset();
    }

    public String parse() {
        String parsed = parser.parse();
        setParsed(parsed);
        return parsed;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public ChangelogGeneratorView view() {
        return ChangelogGeneratorView.get();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    private class Parser {
        private DateTimeFormatter dtf;

        public Parser() {
            String pattern = CHANGELOG_DATE_FORMATTER.get();
            try {
                dtf = DateTimeFormatter.ofPattern(pattern);
            } catch (Exception ex) {
                dtf = DateTimeFormatter.ofPattern(CHANGELOG_DATE_FORMATTER.defaultVal());
            }
        }

        public String parse() {
            Map<ChangeType, List<String>> changelog = processChangelog();
            StringBuilder sb = new StringBuilder();
            if (getVersion().isBlank()) setVersion(view().askVersion());
            sb.append("## ").append("[%s]".formatted(getVersion()))
                .append(" - ")
                .append(LocalDate.now().format(dtf)).append("\n\n");
            for (Map.Entry<ChangeType, List<String>> e : changelog.entrySet()) {
                ChangeType type = e.getKey();
                List<String> changes = e.getValue();
                sb.append("## ").append(type.getName()).append("\n\n");
                for (String change : changes) {
                    sb.append(change);
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        protected Map<ChangeType, List<String>> processChangelog() {
            String changelog = getChangelog();
            Map<ChangeType, List<String>> changes = new TreeMap<>(Comparator.comparingInt(Enum::ordinal));
            List<String> lines = Arrays.stream(changelog.split("\n"))
                .filter(s -> !s.isBlank())
                .toList();
            lines.forEach(l -> {
                setCurrentLine(l);
                ChangeType type = ChangeType.getType(l)
                    .orElseGet(() -> view().askType());
                if (type == ChangeType.IGNORE) return;

                if (type == ChangeType.VERSION) {
                    setVersion(parseVersion(l));
                    return;
                }

                String processed = processLine(l);
                changes.computeIfAbsent(type, t -> new ArrayList<>()).add(processed);
            });
            return changes;
        }

        protected String processLine(String s) {
            int end = s.indexOf(":", s.indexOf(":") + 1);
            return "- " + s.substring(end + 1).trim() + "\n";
        }

        protected String parseVersion(String s) {
            // Method 1, "version xxx"
            Matcher matcher = Pattern.compile("(?i)(?:version \\d+.+)").matcher(s);
            if (matcher.find()) return matcher.group()
                .replace("Version", "")
                .replace("version", "")
                .trim();

            // Method 2, "Release vXXX"
            matcher = Pattern.compile("\\b(?i)[v][\\d.]+").matcher(s);
            if (matcher.find()) return matcher.group()
                .replace("v", "")
                .replace("V", "")
                .trim();

            return view().askVersion();
        }
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public String getChangelog() {
        return changelog.get();
    }

    public ResettableStringProperty changelogProperty() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        reset();
        this.changelog.set(changelog);
    }

    public String getVersion() {
        return version.get();
    }

    public ResettableStringProperty versionProperty() {
        return version;
    }

    public void setVersion(String version) {
        this.version.set(version);
    }

    public String getParsed() {
        return parsed.get();
    }

    public ReadOnlyStringProperty parsedProperty() {
        return parsed.getReadOnlyProperty();
    }

    protected void setParsed(String parsed) {
        this.parsed.set(parsed);
    }

    public String getCurrentLine() {
        return currentLine.get();
    }

    public ReadOnlyStringProperty currentLineProperty() {
        return currentLine.getReadOnlyProperty();
    }

    protected void setCurrentLine(String currentLine) {
        this.currentLine.set(currentLine);
    }
}
