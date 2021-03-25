package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskAction;

import io.bitrise.trace.plugin.TraceConstants;

/**
 * Task that verifies if the Trace SDK is correctly set up or not.
 */
public class VerifyTraceTask extends BaseTraceTask {

    /**
     * The action that will be performed when the task is run. Will verify if Trace SDK is correctly set up. Note: we
     * do not need to explicitly check for the configuration file, as it will be checked during evaluation. For more
     * information about this, please see the linked classes/methods.
     *
     * @see io.bitrise.trace.plugin.configuration.BuildConfigurationManager
     * @see io.bitrise.trace.plugin.TraceGradlePlugin#apply(Project)
     */
    @TaskAction
    public void verifyTrace() {
        verifyTraceGradlePluginDependency(project);
        verifyTraceSdkDependency(project);
    }

    /**
     * Checks if this plugin is correctly set up on the Project or not. Note: we do not need to explicitly report
     * errors, as if the plugin is not applied, the task itself cannot be run.
     *
     * @param project the given Project.
     */
    private void verifyTraceGradlePluginDependency(@NonNull final Project project) {
        final PluginContainer pluginContainer = project.getPlugins();
        for (final Configuration configuration : project.getBuildscript().getConfigurations()) {
            for (final Dependency dependency : configuration.getAllDependencies()) {
                if (TraceConstants.PLUGIN_DEPENDENCY_NAME.equals(dependency.getName()) &&
                        TraceConstants.PLUGIN_DEPENDENCY_GROUP.equals(dependency.getGroup())) {
                    logger.lifecycle("The \"{}\" configuration of project \"{}\" has dependency on " +
                                    "\"{}:{}\" with version \"{}\".",
                            configuration.getName(),
                            project.getName(),
                            dependency.getGroup(),
                            dependency.getName(),
                            dependency.getVersion());
                }
            }
        }
        if (pluginContainer.hasPlugin(TraceConstants.PLUGIN_ID)) {
            logger.lifecycle("Project \"{}\" has applied \"{}\" as plugin.", project.getName(),
                    TraceConstants.PLUGIN_ID);
        }
    }

    /**
     * Checks if the given {@link Project} has dependency on {@link TraceConstants#TRACE_SDK_DEPENDENCY_NAME} or not.
     * If any {@link org.gradle.api.artifacts.Configuration} that should have it contains it, the verification is
     * successful.
     *
     * @param project the given Project.
     */
    private void verifyTraceSdkDependency(@NonNull final Project project) {
        boolean hasSdk = false;
        for (final Configuration configuration : project.getConfigurations()) {
            final String configurationNameLc = configuration.getName().toLowerCase();
            if (configurationNameLc.contains("compileclasspath") || configurationNameLc.contains("runtimeclasspath")) {
                if (hasDependency(configuration, TraceConstants.TRACE_SDK_DEPENDENCY_NAME,
                        TraceConstants.TRACE_SDK_DEPENDENCY_GROUP_NAME)) {
                    hasSdk = true;
                }
            }
        }

        if (!hasSdk) {
            throw new IllegalStateException(String.format("Project %s does not have dependency on %s, verification " +
                    "failed!", project.getName(), TraceConstants.TRACE_SDK_DEPENDENCY_NAME));
        }
    }

    /**
     * Checks if the given {@link Configuration} has dependency on the given dependency or not.
     *
     * @param configuration       the given Configuration.
     * @param dependencyName      the name of the dependency to check for.
     * @param dependencyGroupName the group of the dependency to check for.
     * @return {@code true} if it has, {@code false} otherwise.
     */
    private boolean hasDependency(final Configuration configuration, final String dependencyName,
                                  final String dependencyGroupName) {
        for (final Dependency dependency : configuration.getAllDependencies()) {
            if (dependency.getName().equals(dependencyName) &&
                    dependency.getGroup() != null &&
                    dependency.getGroup().equals(dependencyGroupName)) {
                logger.lifecycle("Configuration \"{}\" contains \"{}\" as dependency with version {}.",
                        configuration.getName(), dependencyName, dependency.getVersion());
                return true;
            }
        }
        return false;
    }
}