package io.bitrise.trace.internal;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Inner class that holds helper methods for updating the CHANGELOG.md.
 */
class ChangeLogHelper {

    private static final Set<String> minorCommitTypes = new HashSet<>(Collections.singletonList("feat"));
    private static final Set<String> patchCommitTypes = new HashSet<>(Collections.singletonList("fix"));
    private static final Set<String> majorCommitTypes = getMajorCommitTypes();
    private static final Set<String> allowedCommitTypes = getAllowedCommitTypes();
    private static final int VERSION_INDEX_PATCH = 0;
    private static final int VERSION_INDEX_MINOR = 1;
    private static final int VERSION_INDEX_MAJOR = 2;

    private static final String messageRegex = "([^:]*):(.*)\n\n((.|\n)*)";
    static final Pattern messagePattern = Pattern.compile(messageRegex);
    private static final String footerRegex = "(\n|\n)APM-[\\d]+(\n|\r|$)";
    static final Pattern footerPattern = Pattern.compile(footerRegex);
    private static final int changeLogInsertLinePos = 8;
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
    void updateChangeLog(final File changeLogFile, final String releaseName,
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
            originalLines.add(changeLogInsertLinePos + 1, UpdateChangeLogTask.maintenanceReleaseEntry);
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
    File getChangeLogFile() {
        return new File("./CHANGELOG.md");
    }
}
