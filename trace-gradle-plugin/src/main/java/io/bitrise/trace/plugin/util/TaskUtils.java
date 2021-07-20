package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.android.build.gradle.tasks.ManifestProcessorTask;
import com.android.build.gradle.tasks.ProcessAndroidResources;
import java.io.File;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

/**
 * Utility class for task related calls.
 */
public class TaskUtils {

  private TaskUtils() {
    throw new IllegalStateException(
        "Should not be instantiated, used only for storing static members!");
  }

  /**
   * Gets the task list for the current build.
   *
   * @param project the {@link Project}.
   * @return the list of the tasks.
   */
  @NonNull
  public static List<Task> getTaskList(@NonNull final Project project) {
    return project.getGradle().getTaskGraph().getAllTasks();
  }

  /**
   * Gets the {@link ManifestProcessorTask} for the given {@link BaseVariantOutput}.
   *
   * @param output the variant output.
   * @return the manifest processor task.
   */
  @NonNull
  public static ManifestProcessorTask getManifestProcessorTask(
      @NonNull final BaseVariantOutput output) {
    return output.getProcessManifestProvider().get();
  }

  /**
   * Gets the {@link ProcessAndroidResources} for the given {@link BaseVariantOutput}.
   *
   * @param output the variant output.
   * @return the process resources task.
   */
  @NonNull
  public static ProcessAndroidResources getResourceProcessorTask(
      @NonNull final BaseVariantOutput output) {
    return output.getProcessResourcesProvider().get();
  }

  /**
   * Gets the assemble {@link Task} for the given {@link BaseVariant}.
   *
   * @param baseVariant the variant.
   * @return the assemble task, or {@code null} when the given build does not have outputs.
   */
  @Nullable
  public static Task getAssembleTask(@NonNull final BaseVariant baseVariant) {
    final TaskProvider<Task> assembleProvider = baseVariant.getAssembleProvider();
    if (assembleProvider == null) {
      return null;
    }
    return baseVariant.getAssembleProvider().get();
  }

  /**
   * Gets the path for the directory for the outputs of the Trace tasks.
   *
   * @param buildDir the build directory of the project.
   * @return the path.
   */
  @NonNull
  public static String getTraceOutputDirPath(@NonNull final File buildDir) {
    return buildDir.getAbsolutePath() + "/outputs/trace";
  }

  /**
   * Gets the path for the directory for the outputs of the Trace tasks for the given variant.
   *
   * @param buildDir    the build directory of the project.
   * @param variantName the name of the variant.
   * @return the path.
   */
  @NonNull
  public static String getTraceOutputDirPath(@NonNull final File buildDir,
                                             @NonNull final String variantName) {
    return getTraceOutputDirPath(buildDir) + "/" + variantName;
  }
}
