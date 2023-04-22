package io.github.palexdev.devutils.parsers;

import io.github.palexdev.devutils.components.dialogs.ChoiceDialog;
import io.github.palexdev.devutils.components.dialogs.DialogsServiceBase;
import io.github.palexdev.devutils.components.dialogs.DialogsServiceBase.DialogConfig;
import io.github.palexdev.devutils.components.dialogs.FieldDialog;
import io.github.palexdev.devutils.enums.ChangeType;
import io.github.palexdev.devutils.parsers.base.ParserBase;
import io.github.palexdev.devutils.settings.ChangelogGeneratorSettings;
import io.github.palexdev.devutils.settings.base.StringSetting;
import io.github.palexdev.devutils.views.ChangelogGeneratorView;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChangelogGeneratorParser extends ParserBase<String> {
    //================================================================================
    // Properties
    //================================================================================
    private String changelog;
    private String version;
    private DateTimeFormatter dtf;

    private String currentLine = "";
    private Consumer<String> onNextLine = l -> {
    };

    private final ChangelogGeneratorSettings settings;
    private final DialogsServiceBase dialogsService;
    private final DialogConfig<ChoiceDialog<ChangeType>> choiceConfig;
    private final DialogConfig<FieldDialog> fieldConfig;

    //================================================================================
    // Constructors
    //================================================================================
    public ChangelogGeneratorParser(ChangelogGeneratorSettings settings, DialogsServiceBase dialogsService) {
        this.settings = settings;
        this.dialogsService = dialogsService;

        // Build DTF and listen for changes to the setting
        dtf = buildDTF();
        settings.onChange(e -> dtf = buildDTF());

        // Build dialogs config
        choiceConfig = new DialogConfig<ChoiceDialog<ChangeType>>()
            .setOnConfigure(c -> {
                c.setItems(ChangeType.values());
                c.getItems().remove(ChangeType.VERSION); // This is unnecessary
                c.setContentText("Change Type");
                c.setSelection(ChangeType.IGNORE);
                c.setDescription(currentLine);
            })
            .implicitOwner();

        fieldConfig = new DialogConfig<FieldDialog>()
            .setOnConfigure(f -> {
                f.setValue("");
                f.setContentText("Input Version");
            })
            .implicitOwner()
            .setShowAlwaysOnTop(false)
            .setShowMinimize(false)
            .setShowClose(false);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public String parse() {
        Map<ChangeType, List<String>> changelog = processChangelog();
        StringBuilder sb = new StringBuilder();
        if (version.isBlank()) version = askVersion();
        sb.append("## ").append("[%s]".formatted(version))
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

    //================================================================================
    // Methods
    //================================================================================
    protected Map<ChangeType, List<String>> processChangelog() {
        Map<ChangeType, List<String>> changes = new TreeMap<>(Comparator.comparingInt(Enum::ordinal));
        List<String> lines = Arrays.stream(changelog.split("\n"))
            .filter(s -> !s.isBlank())
            .toList();
        lines.forEach(l -> {
            currentLine = l;
            onNextLine();

            ChangeType type = ChangeType.getType(l)
                .orElseGet(this::askType);
            if (type == ChangeType.IGNORE) return;

            if (type == ChangeType.VERSION) {
                String pv = parseVersion(l);
                version = (pv != null) ? pv : askVersion();
                return;
            }

            String processed = processLine(l);
            changes.computeIfAbsent(type, t -> new ArrayList<>()).add(processed);
        });
        if (version == null) version = askVersion();
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
        return null;
    }

    protected ChangeType askType() {
        Optional<ChangeType> type = dialogsService.showChoice(ChangelogGeneratorView.class, choiceConfig);
        if (type.isPresent()) return type.get();
        throw new IllegalStateException("Could not get type!");
    }

    protected String askVersion() {
        return dialogsService.showField(ChangelogGeneratorView.class, fieldConfig);
    }

    protected DateTimeFormatter buildDTF() {
        StringSetting setting = settings.dateFormat;
        try {
            return DateTimeFormatter.ofPattern(setting.get());
        } catch (Exception ex) {
            return DateTimeFormatter.ofPattern(setting.defValue());
        }
    }

    protected void onNextLine() {
        if (onNextLine != null) onNextLine.accept(currentLine);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
        version = "";
        currentLine = "";
    }

    public Consumer<String> getOnNextLine() {
        return onNextLine;
    }

    public void setOnNextLine(Consumer<String> onNextLine) {
        this.onNextLine = onNextLine;
    }
}
