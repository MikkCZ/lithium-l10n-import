package cz.mikk.mozilla.sumo.importer.functions;

import cz.mikk.mozilla.sumo.importer.structures.Pair;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

@RequiredArgsConstructor
public class ProcessLanguage implements Function<Pair<Properties, Path>, Pair<Map<Path, Properties>, String>> {

    public static final String REDUNDANT_FILE = "redundant.properties";

    private final Map<String, Path> propertyToFileMapping;

    /**
     * Importer the properties to files based on the mapping given in constructor.
     * @param pair pair of properties and their source path
     * @return pair of filename-properties map and the language code
     */
    @Override
    public Pair<Map<Path, Properties>, String> apply(Pair<Properties, Path> pair) {
        Map<Path, Properties> map = new HashMap<>(propertyToFileMapping.values().size());
        pair.getFirst().entrySet().stream()
                .map(e -> Pair.of(e.getKey().toString(), e.getValue().toString()))
                .forEach(p -> {
                    Path path = propertyToFileMapping.getOrDefault(p.getFirst(), Paths.get(REDUNDANT_FILE));
                    Properties properties = map.computeIfAbsent(path, k -> new Properties());
                    properties.put(p.getFirst(), p.getSecond());
                });
        String fileName = Objects.toString(pair.getSecond().getFileName());
        return Pair.of(map, FilenameUtils.removeExtension(fileName));
    }
}
