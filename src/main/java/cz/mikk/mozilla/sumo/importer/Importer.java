package cz.mikk.mozilla.sumo.importer;

import lombok.Builder;
import nu.studer.java.util.OrderedProperties;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

@Builder
public class Importer {

    private final Path inputDir, sourceLangDir, outputDir;

    public void run() throws IOException {
        Map<String, Map<Path, OrderedProperties>> processedLanguages = processInput(sourceLangDir, inputDir);
        saveOutput(processedLanguages);
    }

    private Map<String, Map<Path, OrderedProperties>> processInput(Path sourceLangDir, Path inputDir) throws IOException {
        return Collections.unmodifiableMap(new InputProcessor().process(sourceLangDir, inputDir));
    }

    private void saveOutput(Map<String, Map<Path, OrderedProperties>> processedLanguages) {
        OutputWriter outputWriter = new OutputWriter(outputDir);
        processedLanguages.entrySet().forEach(entry ->
            outputWriter.write(entry.getKey(), entry.getValue())
        );
    }
}
