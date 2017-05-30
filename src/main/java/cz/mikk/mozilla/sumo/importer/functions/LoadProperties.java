package cz.mikk.mozilla.sumo.importer.functions;

import cz.mikk.mozilla.sumo.importer.structures.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Function;

public class LoadProperties implements Function<Path, Pair<Properties, Path>> {

    /**
     * Load properties from file.
     * @param path the source path
     * @return pair of the properties and their source path
     */
    @Override
    public Pair<Properties, Path> apply(Path path) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(path));
        } catch (IOException ignored) {
            System.err.printf("Cannot load file: \"%s\".%n", path);
        }
        return Pair.of(properties, path);
    }
}
