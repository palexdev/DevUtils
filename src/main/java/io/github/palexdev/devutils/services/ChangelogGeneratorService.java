package io.github.palexdev.devutils.services;

import io.github.palexdev.devutils.parsers.ChangelogGeneratorParser;
import io.github.palexdev.devutils.services.base.ServiceBase;
import io.github.palexdev.mfxcore.base.properties.resettable.ResettableStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Component;

@Component
public class ChangelogGeneratorService extends ServiceBase {
    //================================================================================
    // Static Properties
    //================================================================================
    public static final String DESCRIPTION = """
        This service's purpose it to convert a commit message from libraries such as MaterialFX or VirtualizedFX,
        which use emojis to describe the type of changes, into a changelist that is compatible with the specs
        described on keepachangelog.com.
        """;

    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty changelog = new SimpleStringProperty("") {
        @Override
        protected void invalidated() {
            init();
        }
    };
    private final ResettableStringProperty output = new ResettableStringProperty("", "");

    private final ChangelogGeneratorParser parser;

    //================================================================================
    // Constructors
    //================================================================================
    public ChangelogGeneratorService(ChangelogGeneratorParser parser) {
        this.parser = parser;
        output.setFireChangeOnReset(true);
    }

    //================================================================================
    // Methods
    //================================================================================
    public String parse() {
        String out = getOutput();
        if (out.isBlank()) {
            out = parser.parse();
            setOutput(out);
        }
        return out;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected boolean init() {
        reset();
        parser.setChangelog(getChangelog());
        return true;
    }

    @Override
    protected void reset() {
        parser.setChangelog("");
        setOutput("");
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String getChangelog() {
        return changelog.get();
    }

    public StringProperty changelogProperty() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog.set(changelog);
    }

    public String getOutput() {
        return output.get();
    }

    public ResettableStringProperty outputProperty() {
        return output;
    }

    public void setOutput(String output) {
        this.output.set(output);
    }
}
