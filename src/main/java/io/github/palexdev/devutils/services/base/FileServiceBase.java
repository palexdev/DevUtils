package io.github.palexdev.devutils.services.base;

import io.github.palexdev.devutils.parsers.base.FileParserBase;
import io.github.palexdev.mfxcore.base.properties.resettable.ResettableBooleanProperty;
import io.github.palexdev.mfxcore.base.properties.resettable.ResettableObjectProperty;

import java.io.File;
import java.io.IOException;

public abstract class FileServiceBase<O> extends ServiceBase {
    //================================================================================
    // Properties
    //================================================================================
    protected File file;
    private final ResettableObjectProperty<O> output = new ResettableObjectProperty<>();
    private final ResettableBooleanProperty valid = new ResettableBooleanProperty(false, false);
    protected final FileParserBase<O> parser;

    //================================================================================
    // Constructors
    //================================================================================
    public FileServiceBase(FileParserBase<O> parser) {
        this.parser = parser;
        output.setFireChangeOnReset(true);
        valid.setFireChangeOnReset(true);
    }

    //================================================================================
    // Methods
    //================================================================================
    public O parse() {
        return parser.parse();
    }

    protected void onFileNotValid(Throwable t) {
        t.printStackTrace();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected boolean init() {
        reset();
        if (file == null) return false;
        if (!parser.isFileValid()) {
            onFileNotValid(new IOException("File %s is not valid".formatted(file.getName())));
            return false;
        }
        setValid(true);
        return true;
    }

    @Override
    protected void reset() {
        output.reset();
        valid.reset();
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        parser.setFile(file);
        init();
    }

    public O getOutput() {
        return output.get();
    }

    public ResettableObjectProperty<O> outputProperty() {
        return output;
    }

    public void setOutput(O output) {
        this.output.set(output);
    }

    public boolean isValid() {
        return valid.get();
    }

    public ResettableBooleanProperty validProperty() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid.set(valid);
    }
}
