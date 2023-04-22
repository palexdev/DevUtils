package io.github.palexdev.devutils.parsers.base;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

public abstract class FileParserBase<O> extends ParserBase<O> {
    //================================================================================
    // Properties
    //================================================================================
    protected File file;
    private List<String> lines;

    private Consumer<String> onLineParsed = s -> {};
    private Consumer<O> onParsed = o -> {};

    //================================================================================
    // Methods
    //================================================================================
    public List<String> read() {
        if (lines != null && !lines.isEmpty()) return lines;
        try {
            lines = Files.readAllLines(file.toPath());
            return lines;
        } catch (NullPointerException ignored) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return List.of();
    }

    public boolean isFileValid() {
        return true;
    }

    protected void onLineParsed(String l) {
        if (onLineParsed != null) onLineParsed.accept(l);
    }

    protected void onParsed(O out) {
        if (onParsed != null) onParsed.accept(out);
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        lines = null;
    }

    public List<String> getFileLines() {
        return read();
    }

    public Consumer<String> getOnLineParsed() {
        return onLineParsed;
    }

    public void setOnLineParsed(Consumer<String> onLineParsed) {
        this.onLineParsed = onLineParsed;
    }

    public Consumer<O> getOnParsed() {
        return onParsed;
    }

    public void setOnParsed(Consumer<O> onParsed) {
        this.onParsed = onParsed;
    }
}
