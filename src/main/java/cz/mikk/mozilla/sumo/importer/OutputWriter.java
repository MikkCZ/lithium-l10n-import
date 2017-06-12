package cz.mikk.mozilla.sumo.importer;

import lombok.RequiredArgsConstructor;
import nu.studer.java.util.OrderedProperties;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RequiredArgsConstructor
public class OutputWriter {

    private final Path outputDir;

    /**
     * Write properties files for language.
     * @param childDirectory language name
     * @param files map of filename-properties
     */
    public void write(String childDirectory, Map<Path, OrderedProperties> files) {
        try {
            Path writeDirectory = Files.createDirectories(Paths.get(outputDir.toString(), childDirectory));
            files.entrySet().forEach(entry -> {
                Path outputFile = Paths.get(writeDirectory.toString(), FilenameUtils.getName(entry.getKey().toString()));
                try {
                    entry.getValue().store(Files.newBufferedWriter(Files.createFile(outputFile), StandardCharsets.UTF_8), null);
                } catch (IOException e) {
                    System.err.printf("Error writing output to \"%s\".%n", outputFile);
                }
            });
        } catch (IOException e) {
            System.err.printf("Error creating directory \"%s\".%n", Paths.get(outputDir.toString(), childDirectory));
        }
    }
}
