package cz.mikk.mozilla.sumo.importer;

import cz.mikk.mozilla.sumo.importer.functions.LoadProperties;
import cz.mikk.mozilla.sumo.importer.functions.ProcessLanguage;
import cz.mikk.mozilla.sumo.importer.structures.Pair;
import nu.studer.java.util.OrderedProperties;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InputProcessor {

    /**
     * Process the source language and input directory with other languages.
     * @param sourceLangDir directory, where the source language files are
     * @param inputDir directory, where the other languages files are
     * @return map of language-(filename-properties)
     * @throws IOException when something goes wrong when reading the files
     */
    public Map<String, Map<Path, OrderedProperties>> process(Path sourceLangDir, Path inputDir) throws IOException {
        Pair<Map<String, Path>, Map<Path, Comparator<String>>> propertyToFileMapping = loadPropertyToFileMappingFrom(sourceLangDir);
        return processInput(inputDir, propertyToFileMapping.getFirst(), propertyToFileMapping.getSecond());
    }

    private Pair<Map<String, Path>, Map<Path, Comparator<String>>> loadPropertyToFileMappingFrom(Path sourceDir) throws IOException {
        Set<Pair<OrderedProperties, Path>> propertiesPairs = Files.list(sourceDir)
                .filter(Files::isRegularFile)
                .filter(path -> FilenameUtils.getExtension(path.toString()).equals("properties"))
                .map(new LoadProperties())
                .collect(Collectors.toSet());

        Map<String, Path> mapping = new HashMap<>();
        propertiesPairs.stream()
                .map(pair -> Pair.of(
                            pair.getFirst().stringPropertyNames().stream().map(Object::toString).collect(Collectors.toSet()),
                            pair.getSecond()
                        )
                )
                .forEach(pair ->
                    pair.getFirst().forEach(
                            key -> mapping.computeIfAbsent(key, k -> pair.getSecond())
                    )
                );

        Map<Path, Comparator<String>> comparatorMap = propertiesPairs.stream()
                .collect(Collectors.toMap(
                        Pair::getSecond,
                        pair -> new PropertyKeyOrderComparator((LinkedHashSet<String>) pair.getFirst().stringPropertyNames())
                ));

        return Pair.of(
                Collections.unmodifiableMap(mapping),
                Collections.unmodifiableMap(comparatorMap)
        );
    }

    private Map<String, Map<Path, OrderedProperties>> processInput(Path inputDir, Map<String, Path> propertyToFileMapping, Map<Path, Comparator<String>> keysOrderComparators) throws IOException {
        return Files.list(inputDir)
                .filter(Files::isRegularFile)
                .filter(path -> FilenameUtils.getExtension(path.toString()).equals("properties"))
                .map(new LoadProperties())
                .map(new ProcessLanguage(propertyToFileMapping, keysOrderComparators))
                .collect(Collectors.toMap(
                        Pair::getSecond,
                        Pair::getFirst
                ));
    }
}
