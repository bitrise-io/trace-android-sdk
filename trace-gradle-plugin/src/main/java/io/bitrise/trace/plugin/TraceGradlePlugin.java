package io.bitrise.trace.plugin;

import androidx.annotation.NonNull;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.android.build.gradle.api.TestVariant;
import com.android.build.gradle.tasks.ManifestProcessorTask;
import com.android.build.gradle.tasks.ProcessAndroidResources;
import io.bitrise.trace.plugin.configuration.BuildConfigurationManager;
import io.bitrise.trace.plugin.modifier.TraceTransform;
import io.bitrise.trace.plugin.task.GenerateBuildIdTask;
import io.bitrise.trace.plugin.task.ManifestModifierTask;
import io.bitrise.trace.plugin.task.TaskConfig;
import io.bitrise.trace.plugin.task.UploadMappingFileTask;
import io.bitrise.trace.plugin.task.VerifyTraceTask;
import io.bitrise.trace.plugin.util.TaskUtils;
import io.bitrise.trace.plugin.util.TraceTaskBuilder;
import io.bitrise.trace.plugin.util.TraceVariantTaskBuilder;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

/**
 * Gradle plugin for the Trace SDK.
 *
 * <p>The plugin will:
 * <ul>
 * <li>Modify merged AndroidManifest.xmls to use TraceApplication, if the app has
 * its own Android Application class.</li>
 * <li>Upload mapping files to Trace servers.</li>
 * <li>Create build IDs to identify each build with each mapping file.</li>
 * <li>Loads the build configuration for the bitrise-addons-configuration.json file</li>
 * <li>Adds a verification task to check if the SDK is correctly set up on the project</li>
 * </ul>
 *
 * <p>These will be done with different Gradle tasks that are added to the build process.
 */
public class TraceGradlePlugin implements Plugin<Project> {

  /**
   * The value for the tags in {@link org.gradle.api.logging.Logger} logs.
   */
  public static String LOGGER_TAG = "TraceGradlePlugin";

  /**
   * The {@link BuildConfigurationManager} that configures this {@link Project}.
   */
  @NonNull
  private BuildConfigurationManager buildConfigurationManager;

  /**
   * The entry point of the plugin. It's loaded on the first Gradle sync, or when a build
   * starts. Then it's kept in memory (with the Gradle daemon). After evaluation we set up our
   * tasks. Before that point we have no information about the variants and other required
   * information.
   */
  @Override
  public void apply(@NonNull final Project project) {
    buildConfigurationManager =
        BuildConfigurationManager.getInstance(project.getRootDir().getAbsolutePath());
    beforeEvaluation(project);
    project.afterEvaluate(this::onProjectEvaluated);
  }

  /**
   * Called before project evaluation happens. Sets the Java version to 1.8 and adds
   * InvalidPackage lint exception in the Application that is applied to, to prevent failing
   * because of the GRPC lib dependencies.
   *
   * @param project the given Project.
   * @see <a href="https://github.com/grpc/grpc">https://github.com/grpc/grpc</a>
   */
  private void beforeEvaluation(final @NonNull Project project) {
    if (project.getPlugins().hasPlugin(AppPlugin.class)) {
      final AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
      if (appExtension == null) {
        throw new IllegalStateException(
            "\"apply plugin: 'com.android.application'\" must be added before "
                + "\"apply plugin: 'io.bitrise.trace.plugin'\" in application build.gradle!");
      }
      appExtension.getLintOptions().ignore("InvalidPackage");
      appExtension.getCompileOptions().setSourceCompatibility(JavaVersion.VERSION_1_8);
      appExtension.getCompileOptions().setTargetCompatibility(JavaVersion.VERSION_1_8);
      appExtension.registerTransform(new TraceTransform(appExtension, project));
    }
  }

  /**
   * Called when the {@link Project} has been evaluated.
   *
   * @param project the given Project.
   */
  private void onProjectEvaluated(@NonNull final Project project) {
    if (project.getPlugins().hasPlugin(AppPlugin.class)) {
      final AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
      if (appExtension == null) {
        throw new IllegalStateException("Could not find the application extension.");
      }
      applyPluginOnApplication(project, appExtension);
    }
  }

  /**
   * Applies the plugin on the Android application. See more details in the class Javadoc.
   *
   * @param project      the given Project.
   * @param appExtension the {@link AppExtension} of the Project.
   */
  private void applyPluginOnApplication(@NonNull final Project project,
                                        @NonNull final AppExtension appExtension) {
    buildConfigurationManager.applyBuildConfiguration(appExtension);
    addTraceTasks(project);
    appExtension.getApplicationVariants()
                .forEach(applicationVariant -> addTraceTasksToVariant(project, applicationVariant));
    appExtension.getTestVariants()
                .forEach(testVariant -> addTraceTasksToVariant(project, testVariant));
    appExtension.getUnitTestVariants()
                .forEach(unitTestVariant -> addTraceTasksToVariant(project, unitTestVariant));

    appExtension.getDefaultConfig()
                .resValue("string", TraceConstants.BITRISE_BC_PACKAGE_NAME_KEY,
                    appExtension.getDefaultConfig().getApplicationId());
  }

  /**
   * Adds the tasks of {@link TaskConfig#TRACE_PLUGIN_TASK_GROUP} that are not variant dependent.
   *
   * @param project the Project to add the Tasks.
   */
  private void addTraceTasks(@NonNull final Project project) {
    final TaskContainer taskContainer = project.getTasks();
    new TraceTaskBuilder(taskContainer,
        TaskConfig.TASK_NAME_VERIFY_TRACE, VerifyTraceTask.class)
        .setGroup(TaskConfig.TRACE_PLUGIN_TASK_GROUP)
        .setDescription(TaskConfig.TASK_DESCRIPTION_VERIFY_TRACE)
        .build();
  }

  /**
   * Adds the Trace Gradle tasks to the given project's given variant. This method should
   * contain the calls that should be made for each variant.
   *
   * @param project the {@link Project}.
   * @param variant the {@link BaseVariant}.
   */
  private void addTraceTasksToVariant(@NonNull final Project project,
                                      @NonNull final BaseVariant variant) {
    // Each variant will have one or more outputs; for example APK or multiple AABs.
    // This method will add the Tasks from the Trace SDK that are specific to a given variant.
    // For example, the AndroidManifest.xml modifier task should be variant-specific,
    // because different outputs will have a different AndroidManifest.xml file.
    variant.getOutputs().forEach(
        variantOutput -> addTraceTasksToVariantOutput(project, variant, variantOutput));
    final GenerateBuildIdTask generateBuildIdTask =
        registerGenerateBuildIdTask(project, variant);
    registerUploadMappingFileTask(project, variant, generateBuildIdTask);
  }

  /**
   * Adds the Trace Gradle tasks to the given project's given variant's given output. This
   * method should contain the calls that should be made for each variant output.
   *
   * @param project       the {@link Project}.
   * @param variant       the {@link BaseVariant}.
   * @param variantOutput the {@link BaseVariantOutput}.
   */
  private void addTraceTasksToVariantOutput(@NonNull final Project project,
                                            @NonNull final BaseVariant variant,
                                            @NonNull final BaseVariantOutput variantOutput) {
    registerManifestModifierTask(project, variant, variantOutput);
  }

  /**
   * Registers the {@link ManifestModifierTask} for the builds. Runs after the manifest
   * processor tasks.
   *
   * @param project       the {@link Project}.
   * @param variant       the {@link BaseVariant}.
   * @param variantOutput the {@link BaseVariantOutput}.
   */
  private void registerManifestModifierTask(@NonNull final Project project,
                                            @NonNull final BaseVariant variant,
                                            @NonNull final BaseVariantOutput variantOutput) {
    if (variant instanceof TestVariant) {
      project.getLogger().debug(LOGGER_TAG, "Skipping variant {} for manifest modification.",
          variant.getName());
      return;
    }
    final TaskContainer taskContainer = project.getTasks();

    final ManifestModifierTask manifestModifierTask =
        (ManifestModifierTask) new TraceVariantTaskBuilder(
            taskContainer,
            TaskConfig.TASK_NAME_MANIFEST_MODIFIER,
            ManifestModifierTask.class,
            variant)
            .setVariantOutput(variantOutput)
            .setGroup(TaskConfig.TRACE_PLUGIN_TASK_GROUP)
            .setDescription(TaskConfig.TASK_DESCRIPTION_MANIFEST_MODIFIER)
            .build();
    final ManifestProcessorTask manifestProcessorTask =
        TaskUtils.getManifestProcessorTask(variantOutput);
    manifestProcessorTask.finalizedBy(manifestModifierTask);
    final ProcessAndroidResources resourceProcessorTask =
        TaskUtils.getResourceProcessorTask(variantOutput);
    resourceProcessorTask.mustRunAfter(manifestModifierTask);
    manifestModifierTask.dependsOn(manifestProcessorTask);
  }

  /**
   * Registers the {@link GenerateBuildIdTask} for the builds. Runs after the assemble task.
   *
   * @param project the {@link Project}.
   * @param variant the {@link BaseVariant}.
   * @return the task itself.
   */
  private GenerateBuildIdTask registerGenerateBuildIdTask(@NonNull final Project project,
                                                          @NonNull final BaseVariant variant) {
    final TaskContainer taskContainer = project.getTasks();
    final GenerateBuildIdTask generateBuildIdTask =
        (GenerateBuildIdTask) new TraceVariantTaskBuilder(
            taskContainer,
            TaskConfig.TASK_NAME_GENERATE_BUILD_ID,
            GenerateBuildIdTask.class,
            variant)
            .setGroup(TaskConfig.TRACE_PLUGIN_TASK_GROUP)
            .setDescription(TaskConfig.TASK_DESCRIPTION_GENERATE_BUILD_ID)
            .build();

    final Task assembleTask = TaskUtils.getAssembleTask(variant);
    if (assembleTask != null) {
      assembleTask.finalizedBy(generateBuildIdTask);
      generateBuildIdTask.dependsOn(assembleTask);
    }
    return generateBuildIdTask;
  }

  /**
   * Registers the {@link UploadMappingFileTask} for the builds. Runs after the assemble task.
   *
   * @param project             the {@link Project}.
   * @param variant             the {@link BaseVariant}.
   * @param generateBuildIdTask the {@link GenerateBuildIdTask} that this task will depend on.
   */
  private void registerUploadMappingFileTask(@NonNull final Project project,
                                             @NonNull final BaseVariant variant,
                                             @NonNull
                                             final GenerateBuildIdTask generateBuildIdTask) {
    if (!variant.getBuildType().isMinifyEnabled()) {
      return;
    }
    final TaskContainer taskContainer = project.getTasks();
    final UploadMappingFileTask uploadMappingFileTask =
        (UploadMappingFileTask) new TraceVariantTaskBuilder(
            taskContainer,
            TaskConfig.TASK_NAME_UPLOAD_MAPPING_FILE,
            UploadMappingFileTask.class,
            variant)
            .setGroup(TaskConfig.TRACE_PLUGIN_TASK_GROUP)
            .setDescription(TaskConfig.TASK_DESCRIPTION_UPLOAD_MAPPING_FILE)
            .build();

    final Task assembleTask = TaskUtils.getAssembleTask(variant);
    if (assembleTask != null) {
      assembleTask.finalizedBy(uploadMappingFileTask);
      uploadMappingFileTask.dependsOn(assembleTask);
    }
    uploadMappingFileTask.dependsOn(generateBuildIdTask);
  }
}
