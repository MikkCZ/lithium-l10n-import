package cz.mikk.mozilla.sumo.importer;

import cz.mikk.mozilla.sumo.importer.functions.*;
import cz.mikk.mozilla.sumo.importer.structures.Pair;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class InputProcessor {

    /**
     * Process the source language and input directory with other languages.
     * @param sourceLangDir directory, where the source language files are
     * @param inputDir directory, where the other languages files are
     * @return map of language-(filename-properties)
     * @throws IOException when something goes wrong when reading the files
     */
    public Map<String, Map<Path, Properties>> process(Path sourceLangDir, Path inputDir) throws IOException {
        Map<String, Path> propertyToFileMapping = loadPropertyToFileMappingFrom(sourceLangDir);
        return processInput(inputDir, propertyToFileMapping);
    }

    private Map<String, Path> loadPropertyToFileMappingFrom(Path sourceDir) throws IOException {
        Map<String, Path> mapping = new HashMap<>();
        Files.list(sourceDir)
                .filter(Files::isRegularFile)
                .filter(path -> FilenameUtils.getExtension(path.toString()).equals("properties"))
                .map(new LoadProperties())
                .map(pair -> Pair.of(
                            pair.getFirst().keySet().stream().map(Object::toString).collect(Collectors.toSet()),
                            pair.getSecond()
                        )
                )
                .forEach(pair ->
                    pair.getFirst().forEach(
                            key -> mapping.computeIfAbsent(key, k -> pair.getSecond())
                    )
                );
        return Collections.unmodifiableMap(mapping);
    }

    private Map<String, Map<Path, Properties>> processInput(Path inputDir, Map<String, Path> propertyToFileMapping) throws IOException {
        return Files.list(inputDir)
                .filter(Files::isRegularFile)
                .filter(path -> FilenameUtils.getExtension(path.toString()).equals("properties"))
                .map(new LoadProperties())
                .map(new ProcessLanguage(propertyToFileMapping))
                .collect(Collectors.toMap(
                        Pair::getSecond,
                        Pair::getFirst
                ));
    }
}
