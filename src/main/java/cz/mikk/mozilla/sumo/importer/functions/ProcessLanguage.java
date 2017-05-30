package cz.mikk.mozilla.sumo.importer.functions;

import cz.mikk.mozilla.sumo.importer.structures.Pair;
import lombok.RequiredArgsConstructor;
import nu.studer.java.util.OrderedProperties;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@RequiredArgsConstructor
public class ProcessLanguage implements Function<Pair<OrderedProperties, Path>, Pair<Map<Path, OrderedProperties>, String>> {

    public static final String REDUNDANT_FILE = "redundant.properties";

    private final Map<String, Path> propertyToFileMapping;
    private final Map<Path, Comparator<String>> keysOrderComparators;

    /**
     * Importer the properties to files based on the mapping given in constructor.
     * @param pair pair of properties and their source path
     * @return pair of filename-properties map and the language code
     */
    @Override
    public Pair<Map<Path, OrderedProperties>, String> apply(Pair<OrderedProperties, Path> pair) {
        Map<Path, OrderedProperties> map = new HashMap<>(propertyToFileMapping.values().size());
        pair.getFirst().entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .forEach(p -> {
                    Path path = propertyToFileMapping.getOrDefault(p.getFirst(), Paths.get(REDUNDANT_FILE));
                    OrderedProperties properties = map.computeIfAbsent(path, this::buildProperties);
                    properties.setProperty(p.getFirst(), p.getSecond());
                });
        String fileName = Objects.toString(pair.getSecond().getFileName());
        return Pair.of(map, FilenameUtils.removeExtension(fileName));
    }

    private OrderedProperties buildProperties(Path path) {
        return new OrderedProperties.OrderedPropertiesBuilder()
                .withOrdering(keysOrderComparators.get(path))
                .build();
    }
}
