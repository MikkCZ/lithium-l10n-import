package cz.mikk.mozilla.sumo;

import nu.studer.java.util.OrderedProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.mikk.mozilla.sumo.importer.functions.ProcessLanguage.REDUNDANT_FILE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MainTest {

    private static final PrintStream sout = System.out;
    private static final PrintStream serr = System.err;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path inputDir, sourceLangDir, outDir;
    private OutputStream stdOut, stdErr;

    @Before
    public void setUp() {
        inputDir = Paths.get(getClass().getClassLoader().getResource("testInput/cs.properties").getPath()).getParent();
        sourceLangDir = Paths.get(getClass().getClassLoader().getResource("en/a.properties").getPath()).getParent();
        outDir = Paths.get(temporaryFolder.getRoot().toURI());

        stdOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdOut));
        stdErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(stdErr));
    }

    @After
    public void tearDown() {
        System.setOut(sout);
        System.setErr(serr);

        temporaryFolder.delete();
    }

    @Test
    public void standardStreamsAreEmpty() {
        // Act
        runMain();

        // Assert
        assertThat(stdOut.toString(), isEmptyString());
        assertThat(stdErr.toString(), isEmptyString());
    }

    @Test
    public void outputDirectoriesAreCreated() throws Exception {
        // Act
        runMain();

        // Assert
        Set<Path> foldersNames = Files.list(outDir)
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .collect(Collectors.toSet());
        assertThat(foldersNames, containsInAnyOrder(
                Paths.get("cs"),
                Paths.get("pl")
        ));
    }

    @Test
    public void propertyFilesAreCreated() throws Exception {
        // Act
        runMain();

        // Assert
        Set<Path> csFileNames = Files.list(Paths.get(outDir.toString(), "cs"))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .collect(Collectors.toSet());
        assertThat(csFileNames, containsInAnyOrder(
                Paths.get("a.properties"),
                Paths.get("b.properties"),
                Paths.get("redundant.properties")
        ));

        Set<Path> plFileNames = Files.list(Paths.get(outDir.toString(), "pl"))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .collect(Collectors.toSet());
        assertThat(plFileNames, containsInAnyOrder(
                Paths.get("b.properties"),
                Paths.get("redundant.properties")
        ));
    }

    @Test
    public void propertiesAreInRightFiles() throws Exception {
        // Act
        runMain();

        // Assert
        assertPropertiesFile(Paths.get(outDir.toString(), "cs", "a.properties"), "a.string", "second.string", "third.string", "fourth.string");
        assertPropertiesFile(Paths.get(outDir.toString(), "cs", "b.properties"), "b.string");
        assertPropertiesFile(Paths.get(outDir.toString(), "cs", REDUNDANT_FILE), "c.string");

        assertPropertiesFile(Paths.get(outDir.toString(), "pl", "b.properties"), "b.string");
        assertPropertiesFile(Paths.get(outDir.toString(), "pl", REDUNDANT_FILE), "d.string");
    }

    @Test
    public void keepPropertiesOrder() throws Exception {
        // Act
        runMain();

        // Assert
        assertPropertiesFileInOrder(Paths.get(outDir.toString(), "cs", "a.properties"), "a.string", "second.string", "third.string", "fourth.string");
    }

    @Test
    public void printsErrorWhenTooFewArguments() {
        // Act
        Main.main(new String[]{"1"});

        // Assert
        assertThat(stdErr.toString(), not(isEmptyOrNullString()));
        assertThat(temporaryFolder.getRoot().listFiles().length, is(0));
    }

    @Test
    public void printsErrorWhenTooManyArguments() {
        // Act
        Main.main(new String[]{"1", "2", "3", "4"});

        // Assert
        assertThat(stdErr.toString(), not(isEmptyOrNullString()));
        assertThat(temporaryFolder.getRoot().listFiles().length, is(0));
    }

    @Test
    public void printsErrorWhenInputDirDoesNotExist() {
        // Arrange
        inputDir = Paths.get(outDir.toString(), "non-existing");

        // Act
        runMain();

        // Assert
        assertThat(stdErr.toString(), not(isEmptyOrNullString()));
        assertThat(temporaryFolder.getRoot().listFiles().length, is(0));
    }

    @Test
    public void printsErrorWhenSourceLangDirDoesNotExist() {
        // Arrange
        sourceLangDir = Paths.get(outDir.toString(), "non-existing");

        // Act
        runMain();

        // Assert
        assertThat(stdErr.toString(), not(isEmptyOrNullString()));
        assertThat(temporaryFolder.getRoot().listFiles().length, is(0));
    }

    @Test
    public void createsOutputDirectoryWhenDoesNotExist() {
        // Arrange
        outDir = Paths.get(outDir.toString(), "non-existing");

        // Act
        runMain();

        // Assert
        assertThat(stdErr.toString(), isEmptyString());
        assertThat(temporaryFolder.getRoot().listFiles().length, is(1));
        assertThat(temporaryFolder.getRoot().listFiles()[0].toPath(), equalTo(outDir));
    }

    @Test
    public void warnsAboutNonEmptyOutputDirectory() throws Exception {
        // Arrange
        Files.createFile(Paths.get(outDir.toString(), "some-file.txt"));

        // Act
        runMain();

        // Assert
        assertThat(stdOut.toString(), not(isEmptyOrNullString()));
        assertThat(stdOut.toString(), startsWith("WARNING"));
        assertThat(stdErr.toString(), isEmptyString());
        assertThat(temporaryFolder.getRoot().listFiles().length, is(2+1));
    }

    private void runMain() {
        Main.main(new String[] {
                inputDir.toString(),
                sourceLangDir.toString(),
                outDir.toString(),
        });
    }

    private void assertPropertiesFile(Path path, String... keys) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newBufferedReader(path));
        assertThat(properties.keySet(), containsInAnyOrder(keys));
    }

    private void assertPropertiesFileInOrder(Path path, String... keys) throws IOException {
        OrderedProperties properties = new OrderedProperties();
        properties.load(Files.newBufferedReader(path));
        assertThat(properties.stringPropertyNames(), contains(keys));
    }

}
