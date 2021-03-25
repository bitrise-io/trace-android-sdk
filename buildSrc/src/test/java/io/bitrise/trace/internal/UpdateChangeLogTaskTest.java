package io.bitrise.trace.internal;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.FS;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UpdateChangeLogTask}.
 * <p>
 * Note: you might have issues with running a single test case or this test via the gutter buttons/Android JUnit Test
 * configurations in Android Studio. The issue is "Gradle-Aware Make", for some reason it hangs Android Studio from
 * running test cases this way. To overcome this, you have to remove "Gradle-Aware Make" from the given configuration.
 * Also, if you previously run it without this fix, you have to restart Android Studio.
 * <p>
 * Further note: you might have to redo this after every restart, as "Gradle-Aware Make" is re-added each time to your
 * configurations by Android Studio. See linked Google issue.
 * <p>
 * Additional note: for some reason if you right click on the reports file in build/reports/tests/test/index.html and
 * press "Open in Browser", it will open the test results for root folder tests (InjectTraceTaskTest). Seems like
 * this is an Android Studio/IntelliJ IDEA issue. To overcome this, you have to open manually the file from any
 * compatible application (for example use "Finder" for macOS machines).
 *
 * @see
 * <a href="https://issuetracker.google.com/issues/77840239#comment3">https://issuetracker.google.com/issues/77840239#comment3</a>
 */
public class UpdateChangeLogTaskTest {

    private static final String dummyCommitTemplate = "%s: %s\n\n%s\n\n%s";
    private static final String dummyCommitType1 = "fix";
    private static final String dummyCommitTitle1 = "Some fix";
    private static final String dummyCommitDetails1 = "This was needed.";
    private static final String dummyCommitFooter1 = "APM-12345";
    private static final String dummyCommitMessage1 = String.format(dummyCommitTemplate, dummyCommitType1,
            dummyCommitTitle1, dummyCommitDetails1, dummyCommitFooter1);
    private static final String dummyCommitType2 = "feat";
    private static final String dummyCommitTitle2 = "Some feature";
    private static final String dummyCommitDetails2 = "This was needed.\nAdded cool new features";
    private static final String dummyCommitFooter2 = "APM-23456";
    private static final String dummyCommitMessage2 = String.format(dummyCommitTemplate, dummyCommitType2,
            dummyCommitTitle2, dummyCommitDetails2, dummyCommitFooter2);
    private static final String dummyCommitType3 = "fix";
    private static final String dummyCommitTitle3 = "Some other fix";
    private static final String dummyCommitDetails3 = "This was also needed.";
    private static final String dummyCommitFooter3 = "APM-34567";
    private static final String dummyCommitMessage3 = String.format(dummyCommitTemplate, dummyCommitType3,
            dummyCommitTitle3, dummyCommitDetails3, dummyCommitFooter3);
    private static final String dummyCommitType4 = "feat!";
    private static final String dummyCommitTitle4 = "Breaking change";
    private static final String dummyCommitDetails4 = "API break, new API enables everything!\nUse it " +
            "wisely!\nDeprecated some things";
    private static final String dummyCommitFooter4 = "APM-45678";
    private static final String dummyCommitMessage4 = String.format(dummyCommitTemplate, dummyCommitType4,
            dummyCommitTitle4, dummyCommitDetails4, dummyCommitFooter4);
    private static final String dummyCommitType5 = "chore";
    private static final String dummyCommitTitle5 = "CI update";
    private static final String dummyCommitDetails5 = "New bitrise.yml";
    private static final String dummyCommitFooter5 = "APM-56789";
    private static final String dummyCommitMessage5 = String.format(dummyCommitTemplate, dummyCommitType5,
            dummyCommitTitle5, dummyCommitDetails5, dummyCommitFooter5);
    private static final String dummyTagName1 = "0.0.1";
    private static final String dummyTagName2 = "0.1.0";
    // region InputHelper
    private static final String dummyModuleName1 = "module1";
    private static final String dummyModuleName2 = "module2";
    private static final Project mockRootProject = mock(Project.class);
    // region getting commits
    private static final Project mockProject1 = mock(Project.class);
    private static final Project mockProject2 = mock(Project.class);
    // endRegion
    private static final Project mockEmptyProject = mock(Project.class);
    //endRegion
    /**
     * A temporary folder to create a Git repo for the tests.
     */
    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();
    /**
     * A folder for the local repo.
     */
    private static File localDir;
    /**
     * A folder for the remote repo.
     */
    private static File remoteDir;
    /**
     * The {@link Git} that will be used for testing
     */
    private static Git git;
    // endRegion
    // region CommitTypes
    private final UpdateChangeLogTask.ChangeLogHelper changeLogHelper = new UpdateChangeLogTask.ChangeLogHelper(
            Logging.getLogger(UpdateChangeLogTaskTest.class.getName()));
    private final String dummyReleaseName = "Dummy release";
    private final List<String> changeLogLines = new ArrayList<String>() {{
        add("CHANGES");
        add("=======");
        add("");
    }};
    // region common
    private final Logger dummyLogger = Logging.getLogger(UpdateChangeLogTaskTest.class.getName());
    private final UpdateChangeLogTask.GitHelper gitHelper = new UpdateChangeLogTask.GitHelper(dummyLogger);
    private final UpdateChangeLogTask.InputHelper inputHelper = new UpdateChangeLogTask.InputHelper(dummyLogger);

    /**
     * Initialises a a Git repo in the {@link #tempFolder}. Creates different folders for the local and the remote of
     * the repository.
     *
     * @throws GitAPIException if any Git call fails.
     * @throws IOException     if any I/O error occurs.
     */
    private static void initDummyRepo() throws GitAPIException, IOException {
        remoteDir = new File(tempFolder.getRoot().getPath(), "remote");
        remoteDir.mkdirs();
        RepositoryCache.FileKey fileKey = RepositoryCache.FileKey.exact(remoteDir, FS.DETECTED);
        final Repository remoteRepo = fileKey.open(false);
        remoteRepo.create(true);

        localDir = new File(tempFolder.getRoot().getPath(), "local");
        localDir.mkdirs();
        git = Git.cloneRepository().setURI(remoteRepo.getDirectory().getAbsolutePath()).setDirectory(localDir).call();
    }

    /**
     * Closes the given {@link Repository} of the {@link Git} object.
     */
    private static void closeDummyRepo() {
        git.getRepository().close();
    }

    /**
     * Creates a dummy file in the local repository.
     *
     * @param name the name of the dummy file.
     * @return {@code true} if the create was successful, {@code false} otherwise.
     * @throws IOException if any I/O error occurs.
     */
    private static boolean addDummyFile(final String name) throws IOException {
        return new File(localDir, name).createNewFile();
    }

    /**
     * Commits and pushes all the changed files with the given commit message.
     *
     * @param commitMessage the commit message to use.
     * @throws GitAPIException if any Git call fails.
     */
    private static void commitAndPush(final String commitMessage) throws GitAPIException {
        git.add().addFilepattern(".").call();
        git.commit().setMessage(commitMessage).call();
        git.push().call();
    }

    @BeforeClass
    public static void setupClass() throws GitAPIException, IOException {
        setupDummyRepo();
        setupMockProjects();
    }

    /**
     * Sets up a dummy repo in the {@link #tempFolder} with some commits and tags, for testing purposes.
     *
     * @throws GitAPIException if any Git call fails.
     * @throws IOException     if any I/O error occurs.
     */
    private static void setupDummyRepo() throws GitAPIException, IOException {
        initDummyRepo();

        addDummyFile("something1.txt");
        commitAndPush(dummyCommitMessage1);
        tag(dummyTagName1);

        addDummyFile("something2.txt");
        commitAndPush(dummyCommitMessage2);
        tag(dummyTagName2);

        addDummyFile("something3.txt");
        commitAndPush(dummyCommitMessage3);
        addDummyFile("something4.txt");
        commitAndPush(dummyCommitMessage4);
        addDummyFile("something5.txt");
        commitAndPush(dummyCommitMessage5);

        closeDummyRepo();
    }

    /**
     * Creates a tag on the last commit and pushes it to the remote.
     *
     * @param tagName the name of the tag that will be created.
     * @throws GitAPIException if any Git call fails.
     */
    private static void tag(final String tagName) throws GitAPIException {
        git.tag().setName(tagName).setForceUpdate(true).call();
        git.push().setPushTags().call();
    }

    private static void setupMockProjects() {
        when(mockRootProject.getAllprojects()).thenReturn(new HashSet<>(Arrays.asList(mockProject1, mockProject2)));
        when(mockEmptyProject.getAllprojects()).thenReturn(new HashSet<>());
        when(mockProject1.getProjectDir()).thenReturn(new File(tempFolder.getRoot(), dummyModuleName1));
        when(mockProject2.getProjectDir()).thenReturn(new File(tempFolder.getRoot(), dummyModuleName2));
    }

    // endRegion

    // startRegion change log

    @Test
    public void getMajorCommitTypes_ShouldReturnExpectedValues() {
        final Set<String> actual = UpdateChangeLogTask.getMajorCommitTypes();
        final Set<String> expected = new HashSet<>(Arrays.asList("fix!", "feat!"));
        assertEquals(actual, expected);
    }

    @Test
    public void getMinorCommitTypes_ShouldReturnExpectedValues() {
        final Set<String> actual = UpdateChangeLogTask.getMinorCommitTypes();
        final Set<String> expected = new HashSet<>(Collections.singletonList("feat"));
        assertEquals(actual, expected);
    }

    @Test
    public void getPatchCommitTypes_ShouldReturnExpectedValues() {
        final Set<String> actual = UpdateChangeLogTask.getPatchCommitTypes();
        final Set<String> expected = new HashSet<>(Collections.singletonList("fix"));
        assertEquals(actual, expected);
    }

    @Test
    public void getAllowedCommitTypes_ShouldReturnExpectedValues() {
        final Set<String> actual = UpdateChangeLogTask.getAllowedCommitTypes();
        final Set<String> expected = new HashSet<>(Arrays.asList("fix", "feat", "fix!", "feat!"));
        assertEquals(actual, expected);
    }

    @Test
    public void getAllCommits_ShouldReturnAll() throws IOException {
        final RevWalk allCommits = gitHelper.getAllCommits(git);

        final RevCommit commit5 = allCommits.next();
        assertThat(commit5.getFullMessage(), is(dummyCommitMessage5));

        final RevCommit commit4 = allCommits.next();
        assertThat(commit4.getFullMessage(), is(dummyCommitMessage4));

        final RevCommit commit3 = allCommits.next();
        assertThat(commit3.getFullMessage(), is(dummyCommitMessage3));

        final RevCommit commit2 = allCommits.next();
        assertThat(commit2.getFullMessage(), is(dummyCommitMessage2));

        final RevCommit commit1 = allCommits.next();
        assertThat(commit1.getFullMessage(), is(dummyCommitMessage1));
    }

    @Test
    public void getAllTags_ShouldReturnAll() throws IOException {
        final List<Ref> allTags = gitHelper.getAllTags(git);

        final List<String> actual = allTags.stream().map(Ref::getName).collect(Collectors.toList());
        final List<String> expected = new ArrayList<>();
        expected.add(Constants.R_TAGS + dummyTagName1);
        expected.add(Constants.R_TAGS + dummyTagName2);

        assertThat(actual, is(expected));
    }

    @Test
    public void getLastTag_ShouldReturnLast() throws IOException {
        final String actual = gitHelper.getLastTag(git).getName();
        final String expected = Constants.R_TAGS + dummyTagName2;

        assertThat(actual, is(expected));
    }

    @Test
    public void getNewCommits_ShouldReturnNew() throws IOException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<RevCommit> actual = gitHelper.getNewCommits(git, lastTag);

        final RevWalk allCommits = gitHelper.getAllCommits(git);
        final List<RevCommit> expected = new ArrayList<>();
        expected.add(allCommits.next());
        expected.add(allCommits.next());
        expected.add(allCommits.next());

        assertThat(actual, is(expected));
    }

    @Test
    public void formatCommitToChangeLogEntry_ShouldHaveExpectedFormat() {
        final String actual = changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage1).toString();
        final String expected = "* " + dummyCommitType1 + ": **" + dummyCommitTitle1 + ":** " + dummyCommitDetails1;

        assertThat(actual, is(expected));
    }

    @Test
    public void formatCommitToChangeLogEntry_ShouldBeCaseInSensitive() {
        final String actual = changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage1.toUpperCase())
                                             .toString();
        final String expected =
                "* " + dummyCommitType1 + ": **" + dummyCommitTitle1.toUpperCase() + ":** " + dummyCommitDetails1.toUpperCase();

        assertThat(actual, is(expected));
    }

    @Test
    public void getChangeLogEntries_ShouldFilterNotAllowedCommits() throws IOException {
        final List<RevCommit> newCommits = gitHelper.getNewCommits(git, gitHelper.getLastTag(git));
        final List<UpdateChangeLogTask.ChangeLogEntry> changeLogEntries =
                changeLogHelper.getChangeLogEntries(newCommits);

        final List<String> actual = changeLogEntries.stream().map(UpdateChangeLogTask.ChangeLogEntry::toString).collect(
                Collectors.toList());
        final List<String> expected = new ArrayList<>();
        expected.add(changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage4).toString());
        expected.add(changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage3).toString());

        assertThat(actual, is(expected));
    }

    @Test
    public void getReleaseName_ShouldReturnReleaseName() throws IOException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<RevCommit> newCommits = gitHelper.getNewCommits(git, gitHelper.getLastTag(git));
        final List<UpdateChangeLogTask.ChangeLogEntry> changeLogEntries =
                changeLogHelper.getChangeLogEntries(newCommits);

        final String actual = changeLogHelper.getReleaseName(lastTag, changeLogEntries);
        assertTrue(actual.startsWith("# 1.0.0 - "));
    }

    @Test
    public void getReleaseName_ChoreRelease() throws IOException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<UpdateChangeLogTask.ChangeLogEntry> changeLogEntries = Collections.emptyList();

        final String actual = changeLogHelper.getReleaseName(lastTag, changeLogEntries);
        assertTrue(actual.startsWith("# 0.1.1 -"));
    }

    @Test
    public void getNewVersion_patchVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyTagName1, UpdateChangeLogTask.getPatchCommitTypes());
        assertThat(actual, is("0.0.2"));
    }

    @Test
    public void getNewVersion_minorVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyTagName1, UpdateChangeLogTask.getMinorCommitTypes());
        assertThat(actual, is("0.1.0"));
    }

    @Test
    public void getNewVersion_majorVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyTagName1, UpdateChangeLogTask.getMajorCommitTypes());
        assertThat(actual, is("1.0.0"));
    }

    @Test
    public void removeFooter_ShouldRemoveIfPresent() {
        final String actual = changeLogHelper.removeFooter(dummyCommitMessage1);
        final String expected = String.format(dummyCommitTemplate, dummyCommitType1, dummyCommitTitle1,
                dummyCommitDetails1, "").trim();

        assertThat(actual, is(expected));
    }

    @Test
    public void removeFooter_ShouldDoNothingIfNotPresent() {
        final String expected = String.format(dummyCommitTemplate, dummyCommitType1, dummyCommitTitle1,
                dummyCommitDetails1, "").trim();
        final String actual = changeLogHelper.removeFooter(expected);

        assertThat(actual, is(expected));
    }

    @Test
    public void getUpdatedChangeLogContent_MaintenanceRelease() {
        final List<String> actual = changeLogHelper.getUpdatedChangeLogContent(changeLogLines, dummyReleaseName,
                Collections.emptyList());

        assertThat(actual.get(0), is(changeLogLines.get(0)));
        assertThat(actual.get(1), is(changeLogLines.get(1)));
        assertThat(actual.get(2), is(changeLogLines.get(2)));
        assertThat(actual.get(3), is(dummyReleaseName));
        assertThat(actual.get(4), is(UpdateChangeLogTask.maintenanceReleaseEntry));
    }

    @Test
    public void getUpdatedChangeLogContent_ShouldContainUpdated() throws IOException {
        final List<RevCommit> newCommits = gitHelper.getNewCommits(git, gitHelper.getLastTag(git));
        final List<UpdateChangeLogTask.ChangeLogEntry> changeLogEntries =
                changeLogHelper.getChangeLogEntries(newCommits);
        final List<String> actual = changeLogHelper.getUpdatedChangeLogContent(changeLogLines, dummyReleaseName,
                changeLogEntries);

        assertThat(actual.get(0), is(changeLogLines.get(0)));
        assertThat(actual.get(1), is(changeLogLines.get(1)));
        assertThat(actual.get(2), is(changeLogLines.get(2)));
        assertThat(actual.get(3), is(dummyReleaseName));

        assertThat(actual.get(4), is(changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage4).toString()));
        assertThat(actual.get(5), is(changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage3).toString()));
    }

    @Test
    public void getAvailableModules_ShouldContainAll() {
        final Set<String> actual = inputHelper.getAvailableModules(mockRootProject);
        final Set<String> expected = new HashSet<>(Arrays.asList(dummyModuleName1, dummyModuleName2));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void getAvailableModules_ShouldBeEmpty() {
        final Set<String> actual = inputHelper.getAvailableModules(mockEmptyProject);
        assertThat(actual, containsInAnyOrder(new HashSet<>().toArray()));
    }

    @Test
    public void validateModules_EmptyShouldNotThrowException() {
        final Set<String> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>());
    }

    @Test
    public void validateModules_PartialMatchShouldNotThrowException() {
        final Set<String> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>(Collections.singletonList(dummyModuleName1)));
    }

    @Test
    public void validateModules_AllMatchShouldNotThrowException() {
        final Set<String> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>(Arrays.asList(dummyModuleName2, dummyModuleName1)));
    }

    @Test(expected = IllegalStateException.class)
    public void validateModules_InvalidNameShouldThrowException() {
        final Set<String> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>(Arrays.asList(dummyModuleName2,
                "weDoNotHaveThis")));
    }

    @Test
    public void getModuleDirNamesToUpdate_ShouldBeAvailableModules() {
        final Set<String> expected = inputHelper.getAvailableModules(mockRootProject);
        final Set<String> actual = inputHelper.getModuleDirNamesToUpdate(mockRootProject, new HashSet<>());
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void getModuleDirNamesToUpdate_ShouldBeTheInput() {
        final Set<String> actual = inputHelper.getModuleDirNamesToUpdate(mockRootProject,
                new HashSet<>(Collections.singletonList(dummyModuleName1)));
        assertThat(actual, containsInAnyOrder(dummyModuleName1));
    }

    @Test
    public void getModuleDirNamesToUpdate_ShouldBeAllInput() {
        final Set<String> actual = inputHelper.getModuleDirNamesToUpdate(mockRootProject,
                new HashSet<>(Arrays.asList(dummyModuleName1, dummyModuleName2)));
        assertThat(actual, containsInAnyOrder(dummyModuleName1, dummyModuleName2));
    }

    @Test(expected = IllegalStateException.class)
    public void getModuleDirNamesToUpdate_InvalidInputShouldThrowException() {
        inputHelper.getModuleDirNamesToUpdate(mockRootProject,
                new HashSet<>(Arrays.asList("weDoNotHaveThis", dummyModuleName2)));
    }
    // endRegion
}
