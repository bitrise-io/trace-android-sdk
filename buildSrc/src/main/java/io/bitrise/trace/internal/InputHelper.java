package io.bitrise.trace.internal;

import org.gradle.api.Project;
import org.gradle.api.initialization.IncludedBuild;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Inner class for input processing and validation.
 */
class InputHelper {

    private final Logger logger;

    public InputHelper(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Gets the directories of the modules that should be updated during task run.
     *
     * @param project        the given {@link Project}.
     * @param moduleDirNames the input of module directory names.
     * @return the Set of directories.
     */
    Set<File> getModuleDirsToUpdate(final Project project, final Set<String> moduleDirNames) {
        final Set<File> availableModules = getAvailableModules(project);
        final Set<File> moduleDirsToUpdate;
        if (moduleDirNames == null || moduleDirNames.isEmpty()) {
            logger.lifecycle("Module name was not specified with property \"{}\", task will generate change " +
                    "log entries for all projects", UpdateChangeLogTask.PROPERTY_NAME_MODULES_TO_UPDATE);
            moduleDirsToUpdate = availableModules;
        } else {
            logger.lifecycle("The following input was received:");
            moduleDirNames.forEach(logger::lifecycle);
            validateModules(availableModules, moduleDirNames);
            moduleDirsToUpdate =
                    availableModules.stream()
                                    .filter(it -> moduleDirNames.contains(it.getName()))
                                    .collect(Collectors.toSet());
        }
        return moduleDirsToUpdate;
    }

    /**
     * Validate that the given input module names are in the available Set of names. If any of the input items are
     * not in the available list, this throws IllegalStateException.
     *
     * @param availableModules the Set of available modules.
     * @param moduleDirNames   the Set of modules to update.
     */
    void validateModules(final Set<File> availableModules, final Set<String> moduleDirNames) {
        final Set<String> availableModuleNames =
                availableModules.stream().map(File::getName).collect(Collectors.toSet());
        for (final String moduleDir : moduleDirNames) {
            if (!availableModuleNames.contains(moduleDir)) {
                logger.error("No module found with name \"{}\", task execution is stopping. Please make sure input " +
                                "property \"{}\" is set the the name of the given project's directory, or leave it " +
                                "blank if you want to generate change log entries for all modules", moduleDir,
                        UpdateChangeLogTask.PROPERTY_NAME_MODULES_TO_UPDATE);
                throw new IllegalStateException(String.format("Exception when running task, input property " +
                        "\"%s\" is incorrect", UpdateChangeLogTask.PROPERTY_NAME_MODULES_TO_UPDATE));
            }
        }
    }

    /**
     * Gets the available modules for a given {@link Project}.
     *
     * @param project the given Project.
     * @return the Set of sub project and included build directories.
     */
    Set<File> getAvailableModules(final Project project) {
        return Stream.concat(getProjectModules(project).stream(), getProjectIncludedBuilds(project).stream())
                     .collect(Collectors.toSet());
    }

    /**
     * Gets the submodules of the given {@link Project}.
     *
     * @param project the given Project.
     * @return the Set of sub project directories.
     */
    Set<File> getProjectModules(final Project project) {
        return project.getAllprojects()
                      .stream()
                      .map(Project::getProjectDir)
                      .collect(Collectors.toSet());
    }

    /**
     * Gets the included builds of the given {@link Project}.
     *
     * @param project the given Project.
     * @return the Set of included build directories.
     */
    Set<File> getProjectIncludedBuilds(final Project project) {
        return project.getGradle().getIncludedBuilds()
                      .stream()
                      .map(IncludedBuild::getProjectDir)
                      .collect(Collectors.toSet());
    }
}
