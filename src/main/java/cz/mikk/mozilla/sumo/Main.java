package cz.mikk.mozilla.sumo;

import cz.mikk.mozilla.sumo.importer.Importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {

    /**
     * Runs the tool.
     * @param args input directory (mandatory), source language directory (mandatory), directory for output (optional)
     */
    public static void main(String[] args) {
        Main main = new Main();
        try {
            main.runApplication(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void runApplication(String[] args) throws IllegalArgumentException {
        if (args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException(String.format(
                    "Wrong input arguments, expected: \"%s\".",
                    "INPUT_DIR SOURCE_LANG_DIR [ OUTPUT_DIR ]"
            ));
        }
        try {
            Path inputDir = validateInputDir(args[0]);
            Path sourceLangDir = validateSourceLangDir(args[1]);
            Path outputDirectory = createOutputDirectory(args.length >= 3 ? args[2] : Paths.get("").toAbsolutePath().toString());
            Importer.builder()
                    .inputDir(inputDir)
                    .sourceLangDir(sourceLangDir)
                    .outputDir(outputDirectory)
                    .build()
                    .run();
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalArgumentException(String.format(
                    "The input is invalid: \"%s\".",
                    e.getMessage()
            ));
        }
    }

    private Path validateInputDir(String path) throws IllegalArgumentException {
        return validateDir(path, "The input directory does not exist.");
    }

    private Path validateSourceLangDir(String path) throws IllegalArgumentException {
        return validateDir(path, "The source language directory does not exist.");
    }

    /**
     * Creates output directory if it does not exist.
     * @param path output directory path
     * @return output directory as Path
     * @throws IOException if the directory does not exist and cannot be created
     */
    private Path createOutputDirectory(String path) throws IOException {
        Path p;
        try {
            p = validateDir(path, null);
        } catch (IllegalArgumentException ignored) {
            p = Paths.get(path);
            try {
                Files.createDirectories(p);
            } catch (IOException e) {
                throw new IOException(String.format("Cannot create output directory \"%s\".", path), e);
            }
        }
        if (!Files.list(p).collect(Collectors.toList()).isEmpty()) {
            System.out.printf("WARNING: the output directory \"%s\" is not empty. Colliding files will not be overwritten.%n", p);
        }
        return p;
    }

    /**
     * Checks the path exists and is a directory.
     * @param path directory to check
     * @param errorMessage error message for the exception
     * @return directory as Path
     * @throws IllegalArgumentException if the path does not exists or is not a directory
     */
    private Path validateDir(String path, String errorMessage) throws IllegalArgumentException {
        Path p = Paths.get(path);
        if (!Files.exists(p) || !Files.isDirectory(p)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return p;
    }
}
