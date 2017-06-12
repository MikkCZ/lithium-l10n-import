package cz.mikk.mozilla.sumo.importer.functions;

import cz.mikk.mozilla.sumo.importer.structures.Pair;
import nu.studer.java.util.OrderedProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class LoadProperties implements Function<Path, Pair<OrderedProperties, Path>> {

    /**
     * Load properties from file.
     * @param path the source path
     * @return pair of the properties and their source path
     */
    @Override
    public Pair<OrderedProperties, Path> apply(Path path) {
        OrderedProperties properties = new OrderedProperties();
        try {
            properties.load(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            System.err.printf("Cannot load file: \"%s\".%n", path);
        }
        return Pair.of(properties, path);
    }
}
