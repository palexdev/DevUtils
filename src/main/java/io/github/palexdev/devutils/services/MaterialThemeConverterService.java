package io.github.palexdev.devutils.services;

import io.github.palexdev.devutils.parsers.MaterialThemeConverterParser;
import io.github.palexdev.devutils.services.base.FileServiceBase;
import org.springframework.stereotype.Component;

@Component
public class MaterialThemeConverterService extends FileServiceBase<String> {
    //================================================================================
    // Static Properties
    //================================================================================
    public static final String DESCRIPTION = """
        This service allows to convert Material 3 themes exported as CSS files to SASS.
        From the given CSS tokens, this generates: the palette map, the color schemes maps
        and all the typefaces mixins
        """;

    //================================================================================
    // Constructors
    //================================================================================
    public MaterialThemeConverterService(MaterialThemeConverterParser parser) {
        super(parser);
        outputProperty().set("");
        outputProperty().setDefaultValue("");
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public String parse() {
        String out = getOutput();
        if (out.isBlank()) {
            out = parser.parse();
            setOutput(out);
        }
        return out;
    }

    @Override
    protected boolean init() {
        if (!super.init()) return false;
        onInit(true);
        return true;
    }

    @Override
    protected void reset() {
        super.reset();
        resetLogs();
        onReset();
    }
}
