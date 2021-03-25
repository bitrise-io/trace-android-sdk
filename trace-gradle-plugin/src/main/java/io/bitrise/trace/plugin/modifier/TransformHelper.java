package io.bitrise.trace.plugin.modifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.android.build.api.transform.SecondaryInput;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.BaseExtension;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolveException;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import io.bitrise.trace.plugin.TraceGradlePlugin;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * Contains helper methods for {@link TraceTransform}.
 */
public abstract class TransformHelper {

    @NonNull
    protected final Project project;
    @NonNull
    protected final BaseExtension baseExtension;
    @NonNull
    protected final Logger logger;

    /**
     * Constructor for class.
     *
     * @param project       the given Gradle {@link Project}.
     * @param baseExtension the {@link BaseExtension} of the given extension to transform.
     * @param logger        the {@link Logger} for Gradle.
     * @see
     * <a href=https://developer.android.com/studio/projects/index.html>https://developer.android.com/studio/projects/index.html</a>.
     */
    TransformHelper(@NonNull final Project project, @NonNull final BaseExtension baseExtension,
                    @NonNull final Logger logger) {
        this.project = project;
        this.baseExtension = baseExtension;
        this.logger = logger;
    }

    /**
     * Creates a fully-qualified name from the package name and the application class file name by concatenating them.
     *
     * @param applicationName the name of the Application class.
     * @param packageName     the package name.
     * @return the fully-qualified name of the Application class.
     */
    @Nullable
    static String makeFullyQualifiedName(@NonNull final String applicationName, @NonNull final String packageName) {
        if (applicationName.isEmpty()) {
            return null;
        }
        return packageName.concat(applicationName);
    }

    /**
     * Finds the {@link CtClass} from the {@link ClassPool} with the given name.
     *
     * @param transformInvocation the invocation object containing the transform inputs.
     * @param className           the fully-qualified class name.
     * @return the CtClass with the given name.
     * @throws NotFoundException if no class is found with the given name.
     * @throws IOException       if any I/O error occurs.
     */
    @NonNull
    protected CtClass findClass(@NonNull final TransformInvocation transformInvocation,
                                @NonNull final String className)
            throws IOException, NotFoundException {
        final ClassPool classPool = createClassPool(transformInvocation);
        return classPool.get(className);
    }

    /**
     * Creates the {@link ClassPool} for the given Project. It adds the
     * <ul>
     *    <li>system path</li>
     *    <li>boot class path (android.jar)</li>
     *    <li>directory inputs</li>
     *    <li>jar inputs</li>
     *    <li>secondary inputs</li>
     *    <li>Trace SDK</li>
     * </ul>
     * to the ClassPool.
     *
     * @param transformInvocation the invocation object containing the transform inputs.
     * @return the ClassPool created from the given inputs.
     * @throws IOException if any I/O error occurs.
     */
    @NonNull
    private ClassPool createClassPool(@NonNull final TransformInvocation transformInvocation) throws IOException {
        final ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();

        addBootClassPath(classPool);

        addTransformInputContentToClassPool(classPool, transformInvocation.getReferencedInputs());
        addTransformInputContentToClassPool(classPool, transformInvocation.getInputs());
        addSecondaryInputsToClassPool(classPool, transformInvocation.getSecondaryInputs());
        addTraceToClassPool(classPool);

        return classPool;
    }

    /**
     * Adds the boot ClassPaths of the Application to the given {@link ClassPool}.
     *
     * @param classPool the given ClassPool.
     */
    private void addBootClassPath(@NonNull final ClassPool classPool) {
        baseExtension.getBootClasspath().forEach(file -> addPathToClassPool(classPool, file.getAbsolutePath()));
    }

    /**
     * Adds the given {@link TransformInput}s to the given {@link ClassPool}.
     *
     * @param classPool       the given ClassPool.
     * @param transformInputs the given TransformInputs.
     */
    private void addTransformInputContentToClassPool(@NonNull final ClassPool classPool,
                                                     @NonNull final Collection<TransformInput> transformInputs) {
        transformInputs.forEach(transformInput -> {
            transformInput.getJarInputs()
                          .forEach(jarInput -> addPathToClassPool(classPool, jarInput.getFile().getAbsolutePath()));
            transformInput.getDirectoryInputs()
                          .forEach(directoryInput -> addPathToClassPool(classPool,
                                  directoryInput.getFile().getAbsolutePath()));
        });
    }

    /**
     * Adds the given {@link SecondaryInput}s to tne given {@link ClassPool}.
     *
     * @param classPool       the given ClassPool.
     * @param secondaryInputs the given Collection of SecondaryInputs.
     */
    private void addSecondaryInputsToClassPool(@NonNull final ClassPool classPool,
                                               @NonNull final Collection<SecondaryInput> secondaryInputs) {
        secondaryInputs.forEach(secondaryInput -> secondaryInput.getSecondaryInput()
                                                                .getFileCollection(project)
                                                                .forEach(file -> addPathToClassPool(classPool,
                                                                        file.getAbsolutePath())));
    }

    /**
     * Adds the Trace dependencies to the given {@link ClassPool}.
     *
     * @param classPool the ClassPool to update.
     * @throws IOException if any I/O error occurs.
     */
    private void addTraceToClassPool(@NonNull final ClassPool classPool) throws IOException {
        final Set<ResolvedArtifact> resolvedArtifacts = getResolvedArtifacts();
        for (@NonNull final ResolvedArtifact resolvedArtifact : resolvedArtifacts) {
            if (resolvedArtifact.getName().contains("trace-sdk")) {
                addDependencyToClassPool(classPool, resolvedArtifact);
            }
        }
    }

    /**
     * Adds the given {@link ResolvedArtifact} to the {@link ClassPool}.
     *
     * @param classPool        the ClassPool to update.
     * @param resolvedArtifact the ResolvedArtifact to add.
     * @throws IOException if any I/O error occurs.
     */
    private void addDependencyToClassPool(@NonNull final ClassPool classPool,
                                          @NonNull final ResolvedArtifact resolvedArtifact) throws IOException {
        addDependencyToClassPool(classPool, resolvedArtifact.getFile(), resolvedArtifact.getName());
    }

    /**
     * Adds the given {@link ResolvedArtifact} to the {@link ClassPool}.
     *
     * @param classPool    the ClassPool to update.
     * @param resolvedFile the File of the ResolvedArtifact to add.
     * @param artifactName the name of the ResolvedArtifact.
     * @throws IOException if any I/O error occurs.
     */
    private void addDependencyToClassPool(@NonNull final ClassPool classPool, @NonNull final File resolvedFile,
                                          @NonNull final String artifactName) throws IOException {
        final String absolutePath = resolvedFile.getAbsolutePath();
        if (resolvedFile.isDirectory()) {
            logger.debug("Resolved artifact is a directory at \"{}\"", absolutePath);
            addDirDependencyToClassPool(classPool, artifactName, resolvedFile);
        } else {
            logger.debug("Resolved artifact is a file at \"{}\"", absolutePath);
            if (absolutePath.endsWith(".aar")) {
                addAarDependencyToClassPool(classPool, resolvedFile);
            } else {
                addJarDependencyToClassPool(classPool, absolutePath);
            }
        }
    }

    /**
     * Adds the given AAR File of the {@link ResolvedArtifact} to the {@link ClassPool}.
     *
     * @param classPool    the ClassPool to update.
     * @param resolvedFile the File of the ResolvedArtifact to add.
     * @throws IOException if any I/O error occurs.
     */
    @VisibleForTesting
    void addAarDependencyToClassPool(@NonNull final ClassPool classPool,
                                     @NonNull final File resolvedFile) throws IOException {
        final File extractedClassesJar = extractClassesJarFromAar(resolvedFile);
        addPathToClassPool(classPool, extractedClassesJar.getAbsolutePath());
    }

    /**
     * Adds the given JAR File of the {@link ResolvedArtifact} to the {@link ClassPool}.
     *
     * @param classPool    the ClassPool to update.
     * @param absolutePath the path of the ResolvedArtifact File to add.
     */
    @VisibleForTesting
    void addJarDependencyToClassPool(@NonNull final ClassPool classPool, @NonNull final String absolutePath) {
        addPathToClassPool(classPool, absolutePath);
    }

    /**
     * Adds the given {@link ResolvedArtifact} to the {@link ClassPool} when it is a directory.
     *
     * @param classPool            the ClassPool to update.
     * @param resolvedArtifactName the name of the ResolvedArtifact to add.
     * @param resolvedDir          the Directory of the ResolvedArtifact.
     * @throws IOException if any I/O error occurs.
     */
    @VisibleForTesting
    void addDirDependencyToClassPool(@NonNull final ClassPool classPool,
                                     @NonNull final String resolvedArtifactName,
                                     @NonNull final File resolvedDir) throws IOException {
        final String absolutePath = resolvedDir.getAbsolutePath();
        final String classesJarPath = findClassesJar(absolutePath);
        if (classesJarPath == null) {
            logger.debug("Could not find classes.jar for \"{}\" at path \"{}\".", resolvedArtifactName, absolutePath);
            return;
        }
        addPathToClassPool(classPool, classesJarPath);
    }

    /**
     * Finds the classes.jar File recursively in the given directory.
     *
     * @param rootPath the root path from where the search should start.
     * @return the path of the classes.jar File when found, {@code null} otherwise.
     * @throws IOException if any I/O error occurs.
     */
    @Nullable
    @VisibleForTesting
    String findClassesJar(@NonNull final String rootPath) throws IOException {
        final Optional<Path> classesJar = Files.walk(Paths.get(rootPath))
                                               .filter(file -> file.getFileName()
                                                                   .toFile()
                                                                   .getName()
                                                                   .equals("classes.jar"))
                                               .findAny();
        return classesJar.map(path -> path.toAbsolutePath().toString()).orElse(null);
    }

    /**
     * Gets the {@link ResolvedArtifact}s for the current {@link Project}. Consumes {@link TaskExecutionException}
     * ans {@link IllegalStateException}, when a given {@link org.gradle.api.artifacts.Configuration} is not
     * resolvable, to avoid exceptions thrown by unneeded Configurations.
     *
     * @return the Set of ResolvedArtifacts.
     */
    @NonNull
    private Set<ResolvedArtifact> getResolvedArtifacts() {
        final Set<ResolvedArtifact> resolvedArtifacts = new HashSet<>();
        project.getConfigurations().forEach(configuration -> {
            try {
                final ResolvedConfiguration resolvedConfiguration = configuration.getResolvedConfiguration();
                resolvedArtifacts.addAll(resolvedConfiguration.getResolvedArtifacts());
            } catch (final ResolveException | TaskExecutionException | IllegalStateException e) {
                logger.debug("{}: Failed getting resolved configuration for {}. Reason: {}",
                        TraceGradlePlugin.LOGGER_TAG, configuration.getName(), e.getCause());
            }
        });
        return resolvedArtifacts;
    }

    /**
     * Adds the given path to the {@link ClassPool}. Consumes {@link NotFoundException} when thrown, {@link Logger}
     * prints it as a debug message.
     *
     * @param classPool    the ClassPool to update.
     * @param absolutePath the absolute path to add.
     */
    private void addPathToClassPool(@NonNull final ClassPool classPool, @NonNull final String absolutePath) {
        try {
            classPool.insertClassPath(absolutePath);
        } catch (final NotFoundException e) {
            logger.warn("{}: Cannot insert class path {}. Reason: {}", TraceGradlePlugin.LOGGER_TAG, absolutePath,
                    e.getCause());
        }
    }

    /**
     * Extract the classes.jar File from the given aar File. This is required, as Javassist does not support .aar
     * extension.
     *
     * @param file the given aar File.
     * @return the extracted classes.jar File.
     * @throws IOException if any I/O error occurs.
     */
    @NonNull
    @VisibleForTesting
    File extractClassesJarFromAar(@NonNull final File file) throws IOException {
        try (final JarFile aarFile = new JarFile(file)) {
            final Enumeration<JarEntry> enumEntries = aarFile.entries();
            final File classesJarOutput = new File(file.getParent(), "classes.jar");
            while (enumEntries.hasMoreElements()) {
                final JarEntry aarEntry = enumEntries.nextElement();
                if (aarEntry.getName().equals("classes.jar")) {
                    try (final InputStream inputStream = aarFile.getInputStream(aarEntry)) {
                        try (final FileOutputStream fileOutputStream = new FileOutputStream(classesJarOutput)) {
                            while (inputStream.available() > 0) {
                                fileOutputStream.write(inputStream.read());
                            }
                        }
                    }
                }
            }
            return classesJarOutput;
        }
    }
}
