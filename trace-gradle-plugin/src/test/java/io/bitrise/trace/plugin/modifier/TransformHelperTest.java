package io.bitrise.trace.plugin.modifier;

import androidx.annotation.NonNull;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javassist.ClassPool;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the {@link TransformHelper} class.
 */
public class TransformHelperTest {

    // region common

    private static final TransformHelper mockTransformHelper =
            new ApplicationTransformHelper(mock(Project.class), mock(BaseExtension.class), mock(Logger.class));
    private static final String classesJarName = "classes.jar";
    private static final String dummyAarName = "dummy.aar";
    private static final String randomName = "random";
    private static final String emptyName = "empty";
    private static final String jarsName = "jars";

    private static final String classesJarResourceName = "jar_classes.jar";
    private static final String aarWithClassesJarResourceName = "aar_with_classes_jar.aar";
    private static final String aarWithoutClassesJarResourceName = "aar_without_classes_jar.aar";
    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Copies from the resources a given File to the {@link #tempFolder}, under a new folder. Returns the copied File.
     *
     * @param resourceName the name of the resource to copy.
     * @return the created File.
     * @throws IOException if any I/O occurs.
     */
    private File copyResourceToTempDir(@NonNull final String resourceName) throws IOException {
        return copyResourceToTempDir(resourceName, resourceName);
    }

    /**
     * Copies from the resources a given File to the {@link #tempFolder}, under a new folder. Returns the copied File.
     *
     * @param resourceName the name of the resource to copy.
     * @param copyName     the name of the copy.
     * @return the created File.
     * @throws IOException if any I/O occurs.
     */
    private File copyResourceToTempDir(@NonNull final String resourceName, @NonNull final String copyName)
            throws IOException {
        final File newParentFolder = tempFolder.newFolder();
        final File aarCopy = new File(newParentFolder, copyName);
        Files.copy(Paths.get(getTestFilePath(resourceName)), Paths.get(aarCopy.getAbsolutePath()));
        return aarCopy;
    }

    // endregion

    // region makeFullyQualifiedName tests

    /**
     * Non-empty package and application name should return a valid fully-qualified class name.
     */
    @Test
    public void makeFullyQualifiedName_nonEmptyValuesShouldReturnValid() {
        final String actualValue = TransformHelper.makeFullyQualifiedName(".ApplicationName", "com.example.package");
        assertThat(actualValue, is("com.example.package.ApplicationName"));
    }

    /**
     * Empty application name should return {@code null}.
     */
    @Test
    public void makeFullyQualifiedName_emptyValueShouldReturnInvalid() {
        final String actualValue = TransformHelper.makeFullyQualifiedName("", "com.example.package");
        assertThat(actualValue, is(nullValue()));
    }

    // endregion

    // region addDependencyToClassPool tests


    private static File dummyAarDir;
    private static File randomDir;
    private static File emptyDir;
    private static File jarsDir;
    private static File classesJar;
    private final String resourceDir = "src/test/resources";

    @BeforeClass
    public static void setupClass() throws IOException {
        setupDummyDependencyFiles();
    }

    /**
     * Sets up dummy dependency files for the test cases.
     *
     * @throws IOException if any I/O error occurs.
     */
    private static void setupDummyDependencyFiles() throws IOException {
        dummyAarDir = new File(tempFolder.getRoot(), dummyAarName);
        randomDir = new File(dummyAarDir, randomName);
        emptyDir = new File(dummyAarDir, emptyName);
        jarsDir = new File(randomDir, jarsName);
        classesJar = new File(jarsDir, classesJarName);

        assertTrue(dummyAarDir.mkdir());
        assertTrue(randomDir.mkdir());
        assertTrue(emptyDir.mkdir());
        assertTrue(jarsDir.mkdir());
        assertTrue(classesJar.createNewFile());
    }

    /**
     * Gets a test resource File from the resources directory with the given name.
     *
     * @param fileName the name of the File.
     * @return the relative path of the File.
     */
    private String getTestFilePath(@NonNull final String fileName) {
        return resourceDir + "/" + fileName;
    }

    @Test
    public void findClassesJar_shouldFind() throws IOException {
        final String actual = mockTransformHelper.findClassesJar(dummyAarDir.getAbsolutePath());
        assertThat(actual, is(classesJar.getAbsolutePath()));
    }

    @Test
    public void findClassesJar_shouldNotFind() throws IOException {
        final String actual = mockTransformHelper.findClassesJar(emptyDir.getAbsolutePath());
        assertThat(actual, is(nullValue()));
    }

    // endregion

    // region extractClassesJarFromAar tests

    @Test
    public void addDependencyToClassPool_shouldNotContain() {
        final ClassPool dummyClassPool = new ClassPool();

        final String actual = dummyClassPool.toString();
        assertThat(actual.contains(randomName), is(false));
    }

    @Test
    public void addDirDependencyToClassPool_shouldContain() throws IOException {
        final ClassPool dummyClassPool = new ClassPool();

        final File jarCopy = copyResourceToTempDir(classesJarResourceName, classesJarName);
        mockTransformHelper.addDirDependencyToClassPool(dummyClassPool, randomName, jarCopy.getParentFile());

        final String actual = dummyClassPool.toString();
        assertThat(actual.contains(classesJarName), is(true));
    }

    @Test
    public void addDirDependencyToClassPool_shouldNotContain() throws IOException {
        final ClassPool dummyClassPool = new ClassPool();

        final File newFolder = tempFolder.newFolder();
        mockTransformHelper.addDirDependencyToClassPool(dummyClassPool, randomName, newFolder);

        final String actual = dummyClassPool.toString();
        assertThat(actual.contains(newFolder.getName()), is(false));
    }

    @Test
    public void addJarDependencyToClassPool_shouldNotContain() throws IOException {
        final ClassPool dummyClassPool = new ClassPool();
        final File jarCopy = copyResourceToTempDir(classesJarResourceName);

        mockTransformHelper.addJarDependencyToClassPool(dummyClassPool, jarCopy.getAbsolutePath());
        final String actual = dummyClassPool.toString();
        assertThat(actual.contains(classesJarResourceName), is(true));
    }

    @Test
    public void addAarDependencyToClassPool_shouldNotContain() throws IOException {
        final ClassPool dummyClassPool = new ClassPool();
        final File aarCopy = copyResourceToTempDir(aarWithClassesJarResourceName);

        mockTransformHelper.addAarDependencyToClassPool(dummyClassPool, aarCopy);
        final String actual = dummyClassPool.toString();
        assertThat(actual.contains(classesJarName), is(true));
    }

    @Test
    public void addADependencyToClassPool_shouldNotContain() throws IOException {
        final ClassPool dummyClassPool = new ClassPool();
        final File aarCopy = copyResourceToTempDir(aarWithClassesJarResourceName);

        mockTransformHelper.addAarDependencyToClassPool(dummyClassPool, aarCopy);
        final String actual = dummyClassPool.toString();
        assertThat(actual.contains(classesJarName), is(true));
    }

    @Test
    public void extractClassesJarFromAar_shouldExtract() throws IOException {
        final File aarCopy = copyResourceToTempDir(aarWithClassesJarResourceName);
        final File newParentFolder = aarCopy.getParentFile();

        mockTransformHelper.extractClassesJarFromAar(aarCopy);

        final File expected = new File(newParentFolder, classesJarName);
        assertThat(expected.exists(), is(true));
    }

    @Test
    public void extractClassesJarFromAar_shouldNotExtract() throws IOException {
        final File aarCopy = copyResourceToTempDir(aarWithoutClassesJarResourceName);
        final File newParentFolder = aarCopy.getParentFile();

        mockTransformHelper.extractClassesJarFromAar(aarCopy);

        final File expected = new File(newParentFolder, classesJarName);
        assertThat(expected.exists(), is(false));
    }

    // endregion
}