package io.bitrise.trace.plugin.modifier;

import androidx.annotation.NonNull;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.BaseExtension;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

import io.bitrise.trace.plugin.TraceGradlePlugin;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

/**
 * {@link Transform} that executes the required code modifications for the Trace SDK during the build. It should be
 * applied if there is an Android Application already present in the given Android project. This is required for the
 * automatic initialisation of the Trace SDK.
 * <p>
 * The scope of this Transform is currently only the given Project and it's subprojects. The {@link ClassPool} is
 * initialised based on that. If the scope of the Transform increases, the ClassPool should append the corresponding
 * class paths.
 * <p>
 * The input type for this Transform is currently only classes, as it does not need resources to be modified.
 * <p>
 * If the Transform does not modify a given class, it should copy it, otherwise that would mean it should be omitted
 * from the final output.
 * <p>
 * The Transform uses Javassist for the modifications.
 *
 * @see <a href=http://www.javassist.org>http://www.javassist.org</a>
 */
public class TraceTransform extends Transform {

    @NonNull
    private final BaseExtension baseExtension;
    @NonNull
    private final Logger logger;
    @NonNull
    private final Project project;

    /**
     * Constructor for class.
     *
     * @param baseExtension the {@link BaseExtension}.
     * @param project       the given Project.
     */
    public TraceTransform(@NonNull final BaseExtension baseExtension, @NonNull final Project project) {
        this.baseExtension = baseExtension;
        this.logger = project.getLogger();
        this.project = project;
    }

    @Override
    @NonNull
    public String getName() {
        return "trace";
    }

    @Override
    @NonNull
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return new HashSet<>(Collections.singletonList(QualifiedContent.DefaultContentType.CLASSES));
    }

    @Override
    @NonNull
    public Set<? super QualifiedContent.Scope> getScopes() {
        return new HashSet<>(Arrays.asList(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES));
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(@NonNull final TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        transformInvocation.getOutputProvider().deleteAll();
        copyClassesToTransformOutput(transformInvocation);

        doApplicationTransform(transformInvocation);
        doOkHttpTransform(transformInvocation);
    }

    /**
     * Does the Application class related transforms. Required to initialise our SDK.
     *
     * @param transformInvocation the given {@link TransformInvocation}.
     * @throws TransformException if this transform cannot be done, because it throws an exception.
     * @throws IOException        if any I/O error occurs.
     */
    private void doApplicationTransform(@NonNull final TransformInvocation transformInvocation)
            throws TransformException, IOException {
        try {
            final ApplicationTransformHelper applicationTransformHelper = new ApplicationTransformHelper(project,
                    baseExtension, logger);
            applicationTransformHelper.transformApplicationClasses(transformInvocation, getName(), getOutputTypes(),
                    getScopes());
        } catch (final SAXException | ParserConfigurationException | CannotCompileException e) {
            throw new TransformException(e);
        } catch (final NotFoundException e) {
            logger.debug(
                    "{}: No application class for variant {}, nothing to do. Skipping application class modification.",
                    TraceGradlePlugin.LOGGER_TAG, transformInvocation.getContext().getVariantName());
        }
    }

    /**
     * Does the OkHttp related transforms. Required to intercept OkHttp network calls.
     *
     * @param transformInvocation the given {@link TransformInvocation}.
     * @throws TransformException if this transform cannot be done, because it throws an exception.
     * @throws IOException        if any I/O error occurs.
     */
    private void doOkHttpTransform(@NonNull final TransformInvocation transformInvocation)
            throws TransformException, IOException {
        try {
            final OkHttpTransformHelper okHttpTransformHelper = new OkHttpTransformHelper(project, baseExtension,
                    logger);
            okHttpTransformHelper.updateOkHttpConstructor(transformInvocation, getName(), getOutputTypes(),
                    getScopes());
            okHttpTransformHelper.updateOkHttpBuilder(transformInvocation, getName(), getOutputTypes(),
                    getScopes());
        } catch (final CannotCompileException e) {
            throw new TransformException(e);
        } catch (final NotFoundException e) {
            logger.debug(
                    "{}: No OkHttp dependency for variant {}, nothing to do. Skipping OkHttp class modifications.",
                    TraceGradlePlugin.LOGGER_TAG, transformInvocation.getContext().getVariantName());
        }
    }

    /**
     * Copies all the classes from the {@link TransformInput}s to the output directory of the Transform. This is
     * required for all the inputs, as we do not want to strip out anything from the original set of classes.
     *
     * @param transformInvocation the given {@link TransformInvocation} tht holds the TransformInputs.
     */
    private void copyClassesToTransformOutput(@NonNull final TransformInvocation transformInvocation) {
        final File outputDir = transformInvocation.getOutputProvider().getContentLocation(getName(), getOutputTypes(),
                getScopes(), Format.DIRECTORY);
        if (!outputDir.mkdirs()) {
            logger.error("{}: Failed to create output directory for the transform at {}", TraceGradlePlugin.LOGGER_TAG,
                    outputDir);
        }

        for (@NonNull final TransformInput transformInput : transformInvocation.getInputs()) {
            transformInput.getJarInputs()
                          .forEach(jarInput -> copyJarInput(jarInput, outputDir));

            transformInput.getDirectoryInputs()
                          .forEach(directoryInput -> copyDirectoryInput(directoryInput, outputDir));
        }
    }

    /**
     * Copies the content of the given {@link DirectoryInput} to the given destination. Consumes any IOException that
     * occurs.
     *
     * @param directoryInput the given DirectoryInput.
     * @param outputDir      the given output location.
     */
    private void copyDirectoryInput(@NonNull final DirectoryInput directoryInput, @NonNull final File outputDir) {
        final File baseDir = directoryInput.getFile();
        try (@NonNull final Stream<Path> stream = Files.walk(baseDir.toPath())) {
            stream.forEachOrdered(sourcePath -> {
                try {
                    final Path dstPath = outputDir.toPath().resolve(baseDir.toPath().relativize(sourcePath));
                    Files.copy(sourcePath, dstPath);
                } catch (final IOException e) {
                    logger.debug("{}: Cannot copy directory input for {}. Reason: {}", TraceGradlePlugin.LOGGER_TAG,
                            sourcePath, e.getCause());
                }
            });
        } catch (final IOException e) {
            logger.debug("{}: Cannot walk path {}. Reason: {}", TraceGradlePlugin.LOGGER_TAG,
                    baseDir.toPath().toString(), e.getCause());
        }
    }

    /**
     * Copies the content of the given {@link JarInput} to the given destination. Consumes any IOException that occurs.
     *
     * @param jarInput  the given JarInput.
     * @param outputDir the given output location.
     */
    private void copyJarInput(@NonNull final JarInput jarInput, @NonNull final File outputDir) {
        try {
            extractJar(jarInput.getFile(), outputDir.getPath());
        } catch (final IOException e) {
            logger.debug("{}: Cannot copy JarInput {}. Reason: {}", TraceGradlePlugin.LOGGER_TAG,
                    jarInput.getFile().getPath(), e.getCause());
        }
    }

    /**
     * Extracts the contents of a jar File to the given location.
     *
     * @param file   the given jar file.
     * @param dstDir the given destination.
     * @throws IOException if any I/O error occurs.
     */
    private void extractJar(@NonNull final File file, @NonNull final String dstDir) throws IOException {
        final JarFile jarFile = new JarFile(file);
        final Enumeration<JarEntry> enumEntries = jarFile.entries();
        while (enumEntries.hasMoreElements()) {
            final JarEntry jarEntry = enumEntries.nextElement();
            final File dstFile = new File(dstDir + File.separator + jarEntry.getName());
            if (jarEntry.isDirectory()) {
                if (!dstFile.mkdirs()) {
                    logger.debug("{}: Failed to create directory for {}", TraceGradlePlugin.LOGGER_TAG, dstFile);
                }
                continue;
            }
            final File parent = dstFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                logger.debug("{}: Failed to create directory for {}", TraceGradlePlugin.LOGGER_TAG, dstFile);
            }

            final InputStream inputStream = jarFile.getInputStream(jarEntry);
            final FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
            while (inputStream.available() > 0) {
                fileOutputStream.write(inputStream.read());
            }
            fileOutputStream.close();
            inputStream.close();
        }
        jarFile.close();
    }
}
