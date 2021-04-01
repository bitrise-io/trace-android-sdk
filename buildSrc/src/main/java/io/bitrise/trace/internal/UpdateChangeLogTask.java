package io.bitrise.trace.internal;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.initialization.IncludedBuild;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    private static final Set<String> minorCommitTypes = new HashSet<>(Collections.singletonList("feat"));
    private static final Set<String> patchCommitTypes = new HashSet<>(Collections.singletonList("fix"));
    private static final Set<String> majorCommitTypes = getMajorCommitTypes();
    private static final Set<String> allowedCommitTypes = getAllowedCommitTypes();
    private static final int VERSION_INDEX_PATCH = 0;
    private static final int VERSION_INDEX_MINOR = 1;
    private static final int VERSION_INDEX_MAJOR = 2;
    private final Logger logger;
    private final GitHelper gitHelper;
    public static final String PROPERTY_NAME_MODULES_TO_UPDATE = "moduleDirNames";
    private final InputHelper inputHelper;
    /**
     * An input for the directory name of the module for which this task should generate change logs.
     */
    @Input
    public Set<String> moduleDirNames;
    private static final int changeLogInsertLinePos = 8;

    @Inject
    public UpdateChangeLogTask() {
        this.logger = getProject().getLogger();
        this.gitHelper = new GitHelper(logger);
        this.inputHelper = new InputHelper(logger);
    }

    /**
     * Gets the allowed commit types. They should be in line with the conventional commit types, and only these types
     * should be added to the CHANGELOG.md. Using other commit types is allowed, but they will not be added to the
     * CHANGELOG.md.
     *
     * @see <a href=https://www.conventionalcommits.org/en/v1.0.0/>https://www.conventionalcommits.org/en/v1.0.0/</a>
     */
    static Set<String> getAllowedCommitTypes() {
        return Stream.concat(
                Stream.concat(patchCommitTypes.stream(), minorCommitTypes.stream()),
                majorCommitTypes.stream())
                     .collect(Collectors.toSet());
    }

    /**
     * Gets the major commit types.
     *
     * @return the Set of major commit types.
     */
    static Set<String> getMajorCommitTypes() {
        final Set<String> majorCommitTypes = new HashSet<>();
        minorCommitTypes.forEach(it -> majorCommitTypes.add(it + "!"));
        patchCommitTypes.forEach(it -> majorCommitTypes.add(it + "!"));
        return majorCommitTypes;
    }

    /**
     * Gets the minor commit types.
     *
     * @return the Set of minor commit types.
     */
    static Set<String> getMinorCommitTypes() {
        return minorCommitTypes;
    }

    /**
     * Gets the patch commit types.
     *
     * @return the Set of patch commit types.
     */
    static Set<String> getPatchCommitTypes() {
        return patchCommitTypes;
    }

    /**
     * Does the update of the CHANGELOG.md. All commits since the previous tag will be collected, and the ones with
     * the allowed type ({@link #allowedCommitTypes}) will be added to the CHANGELOG.md.
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

        logger.lifecycle("The following modules will be updated:");
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
        final Git git = gitHelper.getGit();
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

    /**
     * Inner data class for change log entries.
     */
    static final class ChangeLogEntry {

        private final String type;
        private final String title;
        private final String details;

        public ChangeLogEntry(final String type, final String title, final String details) {
            this.type = type;
            this.title = title;
            this.details = details;
        }

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getDetails() {
            return details;
        }

        @Override
        public String toString() {
            return String.format("* %s: **%s:** %s", type, title, details);
        }
    }

    /**
     * Inner class for input processing and validation.
     */
    static class InputHelper {

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
                        "log entries for all projects", PROPERTY_NAME_MODULES_TO_UPDATE);
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
                    logger.error("No module found with name \"{}\", aborting task. Please make sure input property " +
                                    "\"{}\" is set the the name of the given project's directory, or leave it blank " +
                                    "if you want to generate change log entries for all modules", moduleDir,
                            PROPERTY_NAME_MODULES_TO_UPDATE);
                    throw new IllegalStateException(String.format("Aborting build, input property \"%s\" is wrong",
                            PROPERTY_NAME_MODULES_TO_UPDATE));
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

    /**
     * Inner class that holds helper methods for Git related actions.
     */
    static class GitHelper {

        private final Logger logger;

        /**
         * Constructor for class.
         *
         * @param logger a {@link Logger} that will provide log outputs to the console.
         */
        public GitHelper(final Logger logger) {
            this.logger = logger;
        }

        /**
         * Gets the List of {@link RevCommit}s that happened after the given tag.
         *
         * @param git     the {@link Git} repository.
         * @param fromTag the given tag.
         * @return the List of commits.
         * @throws IOException if any I/O error occurs.
         */
        List<RevCommit> getNewCommits(final Git git, final Ref fromTag) throws IOException {
            final RevWalk revWalk = getAllCommitsInIterator(git);
            return getNewCommits(revWalk, getObjectIdForTag(git, fromTag));
        }

        /**
         * Gets the {@link ObjectId} of the given tag.
         *
         * @param git     the {@link Git} repository.
         * @param fromTag the given tag.
         * @return the List of commits.
         * @throws IOException if any I/O error occurs.
         */
        private ObjectId getObjectIdForTag(final Git git, final Ref fromTag) throws IOException {
            final Ref peeledRef = git.getRepository().getRefDatabase().peel(fromTag);
            if (peeledRef.getPeeledObjectId() != null) {
                logger.debug("Using peeled reference ID {} as tag ID.", peeledRef.getPeeledObjectId());
                return peeledRef.getPeeledObjectId();
            } else {
                logger.debug("Using reference ID {} as tag ID.", peeledRef.getObjectId());
                return peeledRef.getObjectId();
            }
        }

        /**
         * Gets the List of {@link RevCommit}s that happened after the given tag.
         *
         * @param revWalk  the related {@link RevWalk}.
         * @param objectId the {@link ObjectId} of the given tag.
         * @return the List of commits.
         * @throws IOException if any I/O error occurs.
         */
        List<RevCommit> getNewCommits(final RevWalk revWalk, final ObjectId objectId) throws IOException {
            RevCommit next = revWalk.next();

            final List<RevCommit> newCommits = new ArrayList<>();
            while (next != null) {
                logger.debug("Checking if commit with message \"{}\" and ID \"{}\" is a tag or not.",
                        next.getShortMessage(), next.getId());
                if (objectId.equals(next.getId())) {
                    logger.debug("Found tag!");
                    break;
                }

                newCommits.add(next);
                next = revWalk.next();
            }
            return newCommits;
        }

        /**
         * Gets the {@link Git} to work with (this repo).
         *
         * @return this Git.
         * @throws IOException if any I/O error occurs.
         */
        Git getGit() throws IOException {
            final Git git = Git.open(new File("./.git"));
            git.checkout();
            return git;
        }

        /**
         * Creates a {@link RevWalk} that contains all the commits on this branch (till the HEAD).
         *
         * @param git the given {@link Git}.
         * @return the created RevWalk.
         * @throws IOException if any I/O error occurs.
         */
        RevWalk getAllCommitsInIterator(final Git git) throws IOException {
            try (final RevWalk revWalk = new RevWalk(git.getRepository())) {
                revWalk.markStart(revWalk.parseCommit(git.getRepository().resolve("HEAD")));
                return revWalk;
            }
        }

        /**
         * Gets the commits as a List of {@link RevCommit}s on this branch (till the HEAD).
         *
         * @param git the given {@link Git}.
         * @return the List of commits.
         * @throws IOException if any I/O error occurs.
         */
        List<RevCommit> getAllCommits(final Git git) throws IOException {
            final List<RevCommit> allCommits = new ArrayList<>();
            getAllCommitsInIterator(git).iterator().forEachRemaining(allCommits::add);
            return allCommits;
        }

        /**
         * Gets the last tag (which was created the latest).
         *
         * @param git the given {@link Git}.
         * @return the {@link Ref} of the tag, or {@code null} when there are no tags.
         * @throws IOException if any I/O error occurs.
         */
        Ref getLastTag(final Git git) throws IOException {
            final List<Ref> allTags = getAllTags(git);
            if (allTags.size() == 0) {
                return null;
            }
            return allTags.get(allTags.size() - 1);
        }

        /**
         * Gets the last tag (which was created the latest).
         *
         * @param git  the given {@link Git}.
         * @param name the name that the tag should contain.
         * @return the {@link Ref} of the tag, or {@code null} when there are no tags with the given name.
         * @throws IOException if any I/O error occurs.
         */
        Ref getLastTag(final Git git, final String name) throws IOException {
            final List<Ref> allTags = getAllTags(git);
            for (int i = 1; i <= allTags.size(); i++) {
                final Ref tag = allTags.get(allTags.size() - i);
                if (tag.getName().contains(name)) {
                    return tag;
                }
            }
            return null;
        }

        /**
         * Gets all tags of a given {@link Git}.
         *
         * @param git the given Git.
         * @return the List of tag {@link Ref}s.
         * @throws IOException if any I/O error occurs.
         */
        List<Ref> getAllTags(final Git git) throws IOException {
            return git.getRepository().getRefDatabase().getRefsByPrefix(Constants.R_TAGS);
        }

        /**
         * Filters the relevant commits that affected the given module.
         *
         * @param git        the {@link Git} of the commits.
         * @param newCommits the {@link RevCommit}s that should be filtered.
         * @param moduleDir  the directory  of the given module.
         * @return a filtered List of the commits.
         * @throws IOException     if any I/O error occurs.
         * @throws GitAPIException if any Git call fails.
         */
        List<RevCommit> filterRelevantCommits(final Git git, final List<RevCommit> newCommits, final File moduleDir)
                throws IOException, GitAPIException {
            final Repository repository = git.getRepository();
            final List<RevCommit> commitsForDiff = addPreviousCommit(git, newCommits);
            final List<ObjectId> objectIdList = commitsForDiff.stream()
                                                              .map(it -> it.getTree().getId())
                                                              .collect(Collectors.toList());

            final List<RevCommit> filteredCommits = new ArrayList<>();
            for (int i = 0; i < objectIdList.size() - 1; i++) {
                final List<DiffEntry> diffEntryList = getCommitDiffList(objectIdList.get(i), objectIdList.get(i + 1),
                        repository);

                if (isModuleAffected(moduleDir, git.getRepository().getWorkTree(), diffEntryList)) {
                    filteredCommits.add(commitsForDiff.get(i));
                }
            }

            return filteredCommits;
        }

        /**
         * Adds the previous commit to the given List of commits
         *
         * @param git     the {@link Git}.
         * @param commits the List of commits.
         * @return the List containing the previous commit.
         * @throws IOException if any I/O error occurs.
         */
        List<RevCommit> addPreviousCommit(final Git git, final List<RevCommit> commits) throws IOException {
            final RevCommit oldestCommit = commits.get(commits.size() - 1);
            final List<RevCommit> allCommits = getAllCommits(git);
            final OptionalInt indexOpt =
                    IntStream.range(0, allCommits.size())
                             .filter(i -> oldestCommit.getId().equals(allCommits.get(i)))
                             .findFirst();
            if (indexOpt.isPresent()) {
                commits.add(allCommits.get(indexOpt.getAsInt() - 1));
            }
            return commits;
        }

        /**
         * Checks if the given module is affected by the given changes or not.
         *
         * @param moduleDir     the directory of the given module.
         * @param rootDir       the root directory of this repo.
         * @param diffEntryList the List of {@link DiffEntry}s (changes).
         * @return {@code true} if yes, {@code false} otherwise.
         * @throws IOException if any I/O error occurs.
         */
        boolean isModuleAffected(final File moduleDir, final File rootDir, final List<DiffEntry> diffEntryList)
                throws IOException {
            for (final DiffEntry diffEntry : diffEntryList) {
                final String oldPath = diffEntry.getOldPath();
                final String newPath = diffEntry.getNewPath();

                if (isModuleParent(moduleDir, rootDir, oldPath) || isModuleParent(moduleDir, rootDir, newPath)) {
                    return true;
                }

                if (isModuleChild(moduleDir, rootDir, oldPath) || isModuleChild(moduleDir, rootDir, newPath)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Checks if a given path is in a subdirectory of the given module.
         *
         * @param moduleDir the directory of the module.
         * @param rootDir   the root directory of this repo.
         * @param path      the given path.
         * @return {@code true} if yes, {@code false} otherwise.
         * @throws IOException if any I/O error occurs.
         */
        boolean isModuleParent(final File moduleDir, final File rootDir, final String path) throws IOException {
            final File changedFile = new File(rootDir, path);
            return changedFile.getCanonicalPath().contains(moduleDir.getCanonicalPath());
        }

        /**
         * Checks if a given path is a in a parent directory of the given module.
         *
         * @param moduleDir the directory of the module.
         * @param rootDir   the root directory of this repo.
         * @param path      the given path.
         * @return {@code true} if yes, {@code false} otherwise.
         * @throws IOException if any I/O error occurs.
         */
        boolean isModuleChild(final File moduleDir, final File rootDir, final String path) throws IOException {
            File givenFile = new File(rootDir, path);
            if (!givenFile.isDirectory()) {
                givenFile = givenFile.getParentFile();
            }

            return moduleDir.getCanonicalPath().contains(givenFile.getCanonicalPath());
        }

        /**
         * Gets the changes between two {@link RevCommit}s.
         *
         * @param head         the {@link ObjectId} of the current HEAD commit.
         * @param previousHead the ObjectId of the previous HEAD commit.
         * @param repository   the affected {@link Repository}.
         * @return the changes as a List of {@link DiffEntry}s.
         * @throws IOException     if any I/O error occurs.
         * @throws GitAPIException if any Git call fails.
         */
        List<DiffEntry> getCommitDiffList(final ObjectId head,
                                          final ObjectId previousHead,
                                          final Repository repository) throws IOException, GitAPIException {
            final List<DiffEntry> diffList;

            try (final ObjectReader reader = repository.newObjectReader()) {
                try (final Git git = new Git(repository)) {
                    diffList = new ArrayList<>(git.diff()
                                                  .setNewTree(getTreeParser(head, reader))
                                                  .setOldTree(getTreeParser(previousHead, reader))
                                                  .call());
                }
            }
            return diffList;
        }

        /**
         * Gets the {@link CanonicalTreeParser} for the given {@link ObjectId}.
         *
         * @param objectId the ID of the HEAD commit.
         * @param reader   the {@link ObjectReader} for the parser.
         * @return the parser.
         * @throws IOException if any I/O error occurs.
         */
        private CanonicalTreeParser getTreeParser(final ObjectId objectId, final ObjectReader reader)
                throws IOException {
            if (objectId == null) {
                return null;
            }

            final CanonicalTreeParser treeParser = new CanonicalTreeParser();
            treeParser.reset(reader, objectId);

            return treeParser;
        }
    }

    /**
     * Inner class that holds helper methods for updating the CHANGELOG.md.
     */
    static class ChangeLogHelper {

        private static final String messageRegex = "([^:]*):(.*)\n\n((.|\n)*)";
        static final Pattern messagePattern = Pattern.compile(messageRegex);
        private static final String footerRegex = "(\n|\n)APM-[\\d]+(\n|\r|$)";
        static final Pattern footerPattern = Pattern.compile(footerRegex);
        private final Logger logger;

        /**
         * Constructor for class.
         *
         * @param logger a {@link Logger} that will provide log outputs to the console.
         */
        public ChangeLogHelper(final Logger logger) {
            this.logger = logger;
        }

        /**
         * Gets the name of the given release. Determines the new version from the change log entries. This is
         * concatenated with the current date.
         *
         * @param lastTag the previous tag.
         * @return the name of the release.
         */
        String getReleaseName(final Ref lastTag, final List<ChangeLogEntry> changeLogEntries) {
            final String previousTagShortName = lastTag.getName().substring(Constants.R_TAGS.length());
            final String versionName = getVersionFromTag(previousTagShortName);
            final String moduleName = getModuleNameFromTag(previousTagShortName);
            logger.debug("The name of the last tag was \"{}\", the version number is \"{}\"", previousTagShortName,
                    versionName);
            final Set<String> entryTypeSet =
                    changeLogEntries.stream().map(ChangeLogEntry::getType).collect(Collectors.toSet());
            entryTypeSet.forEach(type -> logger.debug("Found in new commits type \"{}\"", type));

            return String.format("### %s - %s - %s", moduleName, getNewVersion(versionName, entryTypeSet),
                    getCurrentDate());
        }

        /**
         * Gets the version name from a given tag's name. Tag names should follow the "tagName_x.x.x" format.
         *
         * @param tagName the name of the tag.
         * @return the version name.
         */
        String getVersionFromTag(final String tagName) {
            return tagName.split("_")[1];
        }

        /**
         * Gets the module name from a given tag's name. Tag names should follow the "tagName_x.x.x" format.
         *
         * @param tagName the name of the module.
         * @return the module name.
         */
        String getModuleNameFromTag(final String tagName) {
            return tagName.split("_")[0];
        }

        /**
         * Determines the new version from the change log entries (patch, minor or major release). If there are no
         * changes, it will increase the patch version.
         *
         * @param version the previous version.
         * @param typeSet the Set of types of the change log entries.
         * @return the new version.
         */
        String getNewVersion(final String version, final Set<String> typeSet) {
            final String[] versionNumbers = version.split("\\.");
            if (typeSet.stream().anyMatch(majorCommitTypes::contains)) {
                logger.debug("New version will have major version increase!");
                versionNumbers[VERSION_INDEX_PATCH] = String.valueOf(
                        Integer.parseInt(versionNumbers[VERSION_INDEX_PATCH]) + 1);
                versionNumbers[VERSION_INDEX_MINOR] = "0";
                versionNumbers[VERSION_INDEX_MAJOR] = "0";
            } else if (typeSet.stream().anyMatch(minorCommitTypes::contains)) {
                logger.debug("New version will have minor version increase!");
                versionNumbers[VERSION_INDEX_MINOR] = String.valueOf(
                        Integer.parseInt(versionNumbers[VERSION_INDEX_MINOR]) + 1);
                versionNumbers[VERSION_INDEX_MAJOR] = "0";
            } else {
                logger.debug("New version will have patch version increase!");
                versionNumbers[VERSION_INDEX_MAJOR] = String.valueOf(
                        Integer.parseInt(versionNumbers[VERSION_INDEX_MAJOR]) + 1);
            }
            return String.format("%s.%s.%s", versionNumbers[VERSION_INDEX_PATCH], versionNumbers[VERSION_INDEX_MINOR],
                    versionNumbers[VERSION_INDEX_MAJOR]);
        }

        /**
         * Gets the current date in a YYYY-MM-DD format.
         *
         * @return the String value of the current date.
         */
        private String getCurrentDate() {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(Calendar.getInstance(TimeZone.getDefault()).getTime());
        }

        /**
         * Updates the given change log File, with a new release.
         *
         * @param changeLogFile    the given File.
         * @param releaseName      the name of the newly added release.
         * @param changeLogEntries the List of change log entries.
         * @throws IOException if any I/O error occurs.
         */
        private void updateChangeLog(final File changeLogFile, final String releaseName,
                                     final List<ChangeLogEntry> changeLogEntries) throws IOException {
            final List<String> lines = getFileLines(changeLogFile);
            final List<String> newContent = getUpdatedChangeLogContent(lines, releaseName, changeLogEntries);
            updateFileLines(changeLogFile, newContent);
        }

        /**
         * Gets the content of a given File and returns it as a List of Strings.
         *
         * @param file the given File.
         * @return the content of the File as a List of Strings.
         * @throws IOException if any I/O error occurs.
         */
        private List<String> getFileLines(final File file) throws IOException {
            return Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
        }

        /**
         * Updates the given File with the given List of Strings.
         *
         * @param file       the given File.
         * @param newContent the List of Strings that will be the content of the File.
         * @throws IOException if any I/O error occurs.
         */
        private void updateFileLines(final File file, final List<String> newContent) throws IOException {
            Files.write(Paths.get(file.getPath()), newContent, StandardCharsets.UTF_8);
        }

        /**
         * Creates and returns the new content for a change log file, based on the inputs.
         *
         * @param originalLines the original content of the File, in a List of Strings.
         * @param releaseName   the name of the release that will be added to the CHANGELOG.md.
         * @param newEntries    the new entries that should be appended to the CHANGELOG.md.
         * @return the updated content for the CHANGELOG.md.
         */
        List<String> getUpdatedChangeLogContent(final List<String> originalLines, final String releaseName,
                                                final List<ChangeLogEntry> newEntries) {
            originalLines.add(changeLogInsertLinePos, releaseName);

            final int extraLines;
            if (newEntries.size() == 0) {
                logger.warn(
                        "No commits found, with the allowed types, only adding the release name to the CHANGELOG.md");
                originalLines.add(changeLogInsertLinePos + 1, maintenanceReleaseEntry);
                extraLines = 2;
            } else {
                for (int i = 0; i < newEntries.size(); i++) {
                    originalLines.add(changeLogInsertLinePos + 1 + i, newEntries.get(i).toString());
                }
                extraLines = 1;
            }

            originalLines.add(changeLogInsertLinePos + extraLines + newEntries.size(), "");
            return originalLines;
        }

        /**
         * Formats the given {@link RevCommit}s to readable change log entries.
         *
         * @param commitsToAdd the commits that should be added to the CHANGELOG.md.
         * @return the formatted change log entries.
         */
        List<ChangeLogEntry> getChangeLogEntries(final List<RevCommit> commitsToAdd) {
            return commitsToAdd.stream()
                               .map(it -> formatCommitToChangeLogEntry(it.getFullMessage()))
                               .filter(Objects::nonNull)
                               .collect(Collectors.toList());
        }

        /**
         * Formats a given commit message to readable change log entry. Example message:
         *
         * <pre>
         * feat!: Rename input project_path
         *
         * Renamed input project_path to project_location.
         *
         * APM-2426
         * </pre>
         * <p>
         * Example for the formatted result:
         * <pre>
         *   * feat!: **Rename input project_path:** Renamed input project_path to project_location.
         * </pre>
         *
         * @param commitMessage the given commit message to format.
         * @return the formatted change log entry.
         */
        ChangeLogEntry formatCommitToChangeLogEntry(final String commitMessage) {
            final Matcher matcher = messagePattern.matcher(commitMessage);
            if (matcher.find()) {
                final String commitType = matcher.group(1).trim().toLowerCase();
                final String title = matcher.group(2).trim();
                logger.debug("Commit type is \n{}\n, title is \n{}\n", commitType, title);
                if (allowedCommitTypes.contains(commitType)) {
                    final String messageWithFooter = removeFooter(matcher.group(3).trim());
                    return new ChangeLogEntry(commitType, title, messageWithFooter);
                }
                logger.debug("Skipping commit message with subject \"{}\" as it has a type of {}", title, commitType);
            } else {
                logger.warn(
                        "Could not parse commit message, ignoring. Please add manually to the CHANGELOG.md if it is " +
                                "required! The message:\n{}", commitMessage);
            }
            return null;
        }

        /**
         * Removes the footer from a given message.
         *
         * @param message the given message.
         * @return the message without the footer.
         */
        String removeFooter(final String message) {
            final Matcher matcher = footerPattern.matcher(message);
            return matcher.replaceAll("").trim();
        }

        /**
         * Gets the CHANGELOG.md of this repo.
         *
         * @return the CHANGELOG.md.
         */
        private File getChangeLogFile() {
            return new File("./CHANGELOG.md");
        }
    }
}
