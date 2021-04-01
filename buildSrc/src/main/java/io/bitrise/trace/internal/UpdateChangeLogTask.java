package io.bitrise.trace.internal;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * Internal task for updating the CHANGELOG.md of this repository. Run this task before release, it will update the
 * CHANGELOG.md with the changes made since the last tag.
 * Commits should follow the conventional commits, because this task creates the change log entries based on that.
 * Only commit types "fix", "feat", "fix!" and "feat!" will be inserted to the CHANGELOG.md (these are referred in
 * the code as the allowed commits).
 * When the task is executed, the changes will be uncommitted, you have to commit and push it to the remote
 * repository. Before doing this it is recommended to manually check the changes for any unexpected result, for
 * example for typos, wrong commit types, malformed entries, etc.
 */
public class UpdateChangeLogTask extends DefaultTask {

    static final String maintenanceReleaseEntry = "* Maintenance release, no fixes or new features";

    private final Logger logger;
    private final GitHelper gitHelper;
    public static final String PROPERTY_NAME_MODULES_TO_UPDATE = "moduleDirNames";
    private final InputHelper inputHelper;
    /**
     * An input for the directory name of the module for which this task should generate change logs.
     */
    @Input
    public Set<String> moduleDirNames;

    @Inject
    public UpdateChangeLogTask() {
        this.logger = getProject().getLogger();
        this.gitHelper = new GitHelper(logger);
        this.inputHelper = new InputHelper(logger);
    }

    /**
     * Does the update of the CHANGELOG.md. All commits since the previous tag will be collected, and the ones with
     * the allowed type will be added to the CHANGELOG.md.
     *
     * @throws IOException     if any I/O error occurs.
     * @throws GitAPIException if any Git call fails.
     */
    @TaskAction
    public void taskAction() throws IOException, GitAPIException {
        logger.lifecycle("Starting the update of CHANGELOG.md");
        final Set<File> moduleDirsToUpdate = processInputs();

        for (final File moduleDir : moduleDirsToUpdate) {
            logger.lifecycle("Updating module \"{}\"", moduleDir.getName());
            updateChangeLogWithModule(moduleDir);
        }
    }

    /**
     * Processes the inputs received in {@link #moduleDirNames}.
     *
     * @return the Set of modules that should be updated.
     */
    private Set<File> processInputs() {
        final Set<File> moduleDirsToUpdate = inputHelper.getModuleDirsToUpdate(getProject(), moduleDirNames);

        logger.lifecycle("Task will generate change log entries for the following modules:");
        moduleDirsToUpdate.forEach(it -> logger.lifecycle(it.getName()));

        return moduleDirsToUpdate;
    }

    /**
     * Updates the CHANGELOG.md with the module with the given name.
     *
     * @param moduleDir the module's directory.
     * @throws IOException     if any I/O error occurs.
     * @throws GitAPIException if any Git call fails.
     */
    private void updateChangeLogWithModule(final File moduleDir) throws IOException, GitAPIException {
        final Git git = gitHelper.getGit(getProject().getProjectDir());
        final Ref lastTag = gitHelper.getLastTag(git, moduleDir.getName());
        final List<RevCommit> newCommits = gitHelper.getNewCommits(git, lastTag);
        logger.lifecycle("Found {} commits since last release", newCommits.size());
        if (newCommits.size() == 0) {
            logger.warn("No new commits found, nothing to update, cancelling task");
            return;
        }

        final List<RevCommit> filteredCommits = gitHelper.filterRelevantCommits(git, newCommits, moduleDir);

        final ChangeLogHelper changeLogHelper = new ChangeLogHelper(logger);
        final List<ChangeLogEntry> changeLogEntries = changeLogHelper.getChangeLogEntries(filteredCommits);
        logger.lifecycle("Formatted commit messages to CHANGELOG entries");

        final String releaseName = changeLogHelper.getReleaseName(lastTag, changeLogEntries);
        logger.lifecycle("The name of the release in the CHANGELOG.md will be: {}", releaseName);

        final File changeLogFile = changeLogHelper.getChangeLogFile();
        changeLogHelper.updateChangeLog(changeLogFile, releaseName, changeLogEntries);
        logger.lifecycle("CHANGELOG entries added, finishing task");
    }
}
