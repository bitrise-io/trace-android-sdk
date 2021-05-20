package io.bitrise.trace.plugin.modifier;

import androidx.annotation.NonNull;
import com.android.build.gradle.api.BaseVariantOutput;
import com.android.build.gradle.tasks.ManifestProcessorTask;
import com.android.builder.internal.ClassFieldImpl;
import com.android.builder.model.ClassField;
import io.bitrise.trace.plugin.util.TaskUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;

/**
 * Helper class for build related actions.
 */
public class BuildHelper {

  /**
   * The name for the AndroidManifest.xml files.
   */
  public static final String ANDROID_MANIFEST_FILE_NAME = "AndroidManifest.xml";

  // region Helper methods
  private final Logger logger;

  public BuildHelper(@NonNull final Logger logger) {
    this.logger = logger;
  }

  /**
   * Creates a {@link ClassField} with String type that could be inserted into a BuildConfig.
   *
   * @param key   the key of the String.
   * @param value the value of the String.
   * @return the ClassField.
   * @deprecated as of version 0.0.5, as com.android.tools:gradle have issues with adding
   *     programmatically buildconfig values, please use
   *     {@link #createResValueStringClassField(String, String)}.
   */
  @NonNull
  @Deprecated
  public static ClassField createBuildConfigStringClassField(@NonNull final String key,
                                                             @NonNull final String value) {
    return new ClassFieldImpl("String", key, String.format("\"%1$s\"", value));
  }

  /**
   * Creates a {@link ClassField} with String type that could be inserted into the resource
   * values.
   *
   * @param key   the key of the String.
   * @param value the value of the String.
   * @return the ClassField.
   */
  @NonNull
  public static ClassField createResValueStringClassField(@NonNull final String key,
                                                          @NonNull final String value) {
    return new ClassFieldImpl("string", key, value);
  }

  // endregion

  // region Utility methods

  /**
   * Gets the path for the compiled AndroidManifest.xml files.
   *
   * @param output the build variant output.
   * @return the list of manifest files.
   */
  @NonNull
  public List<File> getManifestPaths(@NonNull final BaseVariantOutput output) {
    final ManifestProcessorTask manifestProcessorTask = TaskUtils.getManifestProcessorTask(output);
    final FileCollection manifestOutputs = manifestProcessorTask.getOutputs().getFiles();

    final List<File> files = collectFilesRecursively(manifestOutputs.getFiles());

    return files.stream()
                .filter(file -> file.getPath().contains("/merged_manifests/"))
                .filter(file -> file.getName().equals(ANDROID_MANIFEST_FILE_NAME))
                .collect(Collectors.toList());
  }

  /**
   * Collects files recursively from a given set of File input. Silently catches IOExceptions
   * and logs them.
   *
   * @param fileSet the given Set of Files.
   * @return the List of collected Files.
   */
  @NonNull
  public List<File> collectFilesRecursively(@NonNull final Set<File> fileSet) {
    final List<File> files = new ArrayList<>();
    fileSet.forEach(it -> {
      try {
        files.addAll(Files.walk(Paths.get(it.getPath()))
                          .filter(Files::isRegularFile)
                          .map(Path::toFile)
                          .collect(Collectors.toList()));
      } catch (final IOException e) {
        logger.warn("Exception happened during manifest output collection.", e);
      }
    });

    return files;
  }

  //endregion
}
