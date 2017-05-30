package cz.mikk.mozilla.sumo.importer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

@RequiredArgsConstructor
public class OutputWriter {

    private final Path outputDir;

    /**
     * Write properties files for language.
     * @param childDirectory language name
     * @param files map of filename-properties
     * @throws IOException if something goes wrong when creating the language output directory
     */
    public void write(String childDirectory, Map<Path, Properties> files) {
        try {
            Path writeDirectory = Files.createDirectories(Paths.get(outputDir.toString(), childDirectory));
            files.entrySet().forEach(entry -> {
                Path outputFile = Paths.get(writeDirectory.toString(), FilenameUtils.getName(entry.getKey().toString()));
                try {
                    entry.getValue().store(Files.newBufferedWriter(Files.createFile(outputFile), Charset.forName("UTF-8")), null);
                } catch (IOException e) {
                    System.err.printf("Error writing output to \"%s\".%n", outputFile);
                }
            });
        } catch (IOException e) {
            System.err.printf("Error creating directory \"%s\".%n", Paths.get(outputDir.toString(), childDirectory));
        }
    }
}
