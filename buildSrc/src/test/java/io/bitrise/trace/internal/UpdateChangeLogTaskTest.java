package io.bitrise.trace.internal;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.FS;
import org.gradle.api.Project;
import org.gradle.api.initialization.IncludedBuild;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UpdateChangeLogTask}.
 * <p>
 * Note: you might have issues with running a single test case or this test via the gutter buttons/Android JUnit Test
 * configurations in Android Studio. The issue is "Gradle-Aware Make", for some reason it hangs Android Studio from
 * running test cases this way. To overcome this, you have to remove "Gradle-Aware Make" from the given configuration.
 * Also, if you previously run it without this fix, you may have to restart Android Studio.
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

    // region common
    private static final String dummyCommitTemplate = "%s: %s\n\n%s\n\n%s";
    private static final String dummyCommitType1 = "fix";
    private static final String dummyCommitTitle1 = "Title 1. Some fix";
    private static final String dummyCommitDetails1 = "This was needed.";
    private static final String dummyCommitFooter1 = "APM-12345";
    private static final String dummyCommitMessage1 = String.format(dummyCommitTemplate, dummyCommitType1,
            dummyCommitTitle1, dummyCommitDetails1, dummyCommitFooter1);
    private static final String dummyCommitType2 = "feat";
    private static final String dummyCommitTitle2 = "Title 2. Some feature";
    private static final String dummyCommitDetails2 = "This was needed.\nAdded cool new features";
    private static final String dummyCommitFooter2 = "APM-23456";
    private static final String dummyCommitMessage2 = String.format(dummyCommitTemplate, dummyCommitType2,
            dummyCommitTitle2, dummyCommitDetails2, dummyCommitFooter2);
    private static final String dummyCommitType3 = "fix";
    private static final String dummyCommitTitle3 = "Title 3. Some other fix";
    private static final String dummyCommitDetails3 = "This was also needed. Modified previous file!";
    private static final String dummyCommitFooter3 = "APM-34567";
    private static final String dummyCommitMessage3 = String.format(dummyCommitTemplate, dummyCommitType3,
            dummyCommitTitle3, dummyCommitDetails3, dummyCommitFooter3);
    private static final String dummyCommitType4 = "feat!";
    private static final String dummyCommitTitle4 = "Title 4. Breaking change";
    private static final String dummyCommitDetails4 = "API break, new API enables everything!\nUse it " +
            "wisely!\nDeprecated some things. Removed file!";
    private static final String dummyCommitFooter4 = "APM-45678";
    private static final String dummyCommitMessage4 = String.format(dummyCommitTemplate, dummyCommitType4,
            dummyCommitTitle4, dummyCommitDetails4, dummyCommitFooter4);
    private static final String dummyCommitType5 = "chore";
    private static final String dummyCommitTitle5 = "Title 5. CI update";
    private static final String dummyCommitDetails5 = "New bitrise.yml";
    private static final String dummyCommitFooter5 = "APM-56789";
    private static final String dummyCommitMessage5 = String.format(dummyCommitTemplate, dummyCommitType5,
            dummyCommitTitle5, dummyCommitDetails5, dummyCommitFooter5);
    private static final String dummyCommitType6 = "test";
    private static final String dummyCommitTitle6 = "Title 6. Add test";
    private static final String dummyCommitDetails6 = "Some new test case added.";
    private static final String dummyCommitFooter6 = "APM-67890";
    private static final String dummyCommitMessage6 = String.format(dummyCommitTemplate, dummyCommitType6,
            dummyCommitTitle6, dummyCommitDetails6, dummyCommitFooter6);

    private static final String dummyModuleName1 = "module1";
    private static final String dummyVersionName1 = "0.0.1";
    private static final String dummyModuleName2 = "module2";
    private static final String dummyVersionName2 = "0.1.0";
    private static final String dummyTagName1 = dummyModuleName1 + "_" + dummyVersionName1;
    private static final String dummyTagName2 = dummyModuleName2 + "_" + dummyVersionName2;
    private static final String dummyFileName1 = "something1.txt";
    private static final String dummyFileName2 = "something2.txt";

    private static final Project mockRootProject = mock(Project.class);
    private static final Gradle mockGradle = mock(Gradle.class);

    private static final Project mockProject1 = mock(Project.class);
    private static final IncludedBuild mockProject2 = mock(IncludedBuild.class);

    private static final Project mockEmptyProject = mock(Project.class);
    private static final Gradle mockEmptyGradle = mock(Gradle.class);

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

    private final UpdateChangeLogTask.ChangeLogHelper changeLogHelper = new UpdateChangeLogTask.ChangeLogHelper(
            Logging.getLogger(UpdateChangeLogTaskTest.class.getName()));
    private final String dummyReleaseName = "Dummy release";
    private final List<String> changeLogLines = new ArrayList<String>() {{
        add("CHANGES");
        add("=======");
        add("");
        add("trace-android-sdk public beta versions");
        add("--------------------------------------");
        add("**Note:** these versions of the *trace-android-sdk* are stored in a public repo, but should be");
        add("considered still as beta.");
        add("");
    }};

    private final Logger dummyLogger = Logging.getLogger(UpdateChangeLogTaskTest.class.getName());
    private final UpdateChangeLogTask.GitHelper gitHelper = new UpdateChangeLogTask.GitHelper(dummyLogger);
    private final UpdateChangeLogTask.InputHelper inputHelper = new UpdateChangeLogTask.InputHelper(dummyLogger);
    private static final String dummyFileName3 = "something3.txt";
    private static final String dummyFileName4 = "something4.txt";
    private static File dummyModule1;
    private static File dummyModule2;

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
     * @param moduleName the name of the module to add to. Leave blank to add it to the root.
     * @param name       the name of the dummy file.
     * @return {@code true} if the create was successful, {@code false} otherwise.
     * @throws IOException if any I/O error occurs.
     */
    private static boolean addDummyFile(final String moduleName, final String name) throws IOException {
        final File toAdd = new File(localDir, moduleName + "/" + name);
        final boolean result = toAdd.createNewFile();
        assert (toAdd.exists());
        return result;
    }

    /**
     * Removes a dummy file in the local repository.
     *
     * @param moduleName the name of the module that holds the file. Leave blank for the root.
     * @param name       the name of the dummy file.
     * @return {@code true} if the remove was successful, {@code false} otherwise.
     */
    private static boolean removeDummyFile(final String moduleName, final String name) {
        final File toDelete = new File(localDir, moduleName + "/" + name);
        assertTrue(toDelete.exists());
        final boolean result = toDelete.delete();
        assert (!toDelete.exists());
        return result;
    }

    /**
     * Modifies a dummy file in the local repository.
     *
     * @param moduleName the name of the module that holds the file. Leave blank for the root.
     * @param name       the name of the dummy file.
     * @throws IOException if any I/O error occurs.
     */
    private static void modifyDummyFile(final String moduleName, final String name) throws IOException {
        final File toModify = new File(localDir, moduleName + "/" + name);
        try (final FileWriter fileWriter = new FileWriter(toModify)) {
            fileWriter.append("newText");
        }
    }

    /**
     * Commits and pushes all the changed files with the given commit message.
     *
     * @param commitMessage the commit message to use.
     * @throws GitAPIException if any Git call fails.
     */
    private static void commitAndPush(final String commitMessage) throws GitAPIException {
        git.add().addFilepattern(".").call();
        git.add().addFilepattern(".").setUpdate(true).call();
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

        dummyModule1 = new File(localDir, dummyModuleName1);
        assertTrue(dummyModule1.mkdirs());
        dummyModule2 = new File(localDir, dummyModuleName2);
        assertTrue(dummyModule2.mkdirs());

        addDummyFile("", dummyFileName1);
        commitAndPush(dummyCommitMessage1);
        tag(dummyTagName1);

        addDummyFile(dummyModuleName1, dummyFileName2);
        commitAndPush(dummyCommitMessage2);
        tag(dummyTagName2);

        addDummyFile(dummyModuleName2, dummyFileName3);
        commitAndPush(dummyCommitMessage3);
        modifyDummyFile(dummyModuleName2, dummyFileName3);
        commitAndPush(dummyCommitMessage4);
        removeDummyFile(dummyModuleName1, dummyFileName2);
        commitAndPush(dummyCommitMessage5);
        addDummyFile("", dummyFileName4);
        commitAndPush(dummyCommitMessage6);

        final Status status = git.status().call();
        assertFalse(status.hasUncommittedChanges());
        assertThat(status.getUntracked().size(), is(0));

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
        when(mockRootProject.getAllprojects()).thenReturn(new HashSet<>(Collections.singletonList(mockProject1)));
        when(mockEmptyProject.getAllprojects()).thenReturn(new HashSet<>());
        when(mockProject1.getProjectDir()).thenReturn(new File(tempFolder.getRoot(), dummyModuleName1));
        when(mockProject2.getProjectDir()).thenReturn(new File(tempFolder.getRoot(), dummyModuleName2));

        when(mockRootProject.getGradle()).thenReturn(mockGradle);
        when(mockGradle.getIncludedBuilds()).thenReturn(new HashSet<>(Collections.singletonList(mockProject2)));

        when(mockEmptyProject.getGradle()).thenReturn(mockEmptyGradle);
        when(mockEmptyGradle.getIncludedBuilds()).thenReturn(new HashSet<>(Collections.emptyList()));
    }

    // endRegion

    // startRegion GitHelper tests

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
    public void getAllCommitsInIterator_ShouldReturnAll() throws IOException {
        final RevWalk allCommits = gitHelper.getAllCommitsInIterator(git);

        final RevCommit commit6 = allCommits.next();
        assertThat(commit6.getFullMessage(), is(dummyCommitMessage6));

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
    public void getAllCommits_ShouldReturnAll() throws IOException {
        final List<RevCommit> allCommits = gitHelper.getAllCommits(git);

        final RevCommit commit6 = allCommits.get(0);
        assertThat(commit6.getFullMessage(), is(dummyCommitMessage6));

        final RevCommit commit5 = allCommits.get(1);
        assertThat(commit5.getFullMessage(), is(dummyCommitMessage5));

        final RevCommit commit4 = allCommits.get(2);
        assertThat(commit4.getFullMessage(), is(dummyCommitMessage4));

        final RevCommit commit3 = allCommits.get(3);
        assertThat(commit3.getFullMessage(), is(dummyCommitMessage3));

        final RevCommit commit2 = allCommits.get(4);
        assertThat(commit2.getFullMessage(), is(dummyCommitMessage2));

        final RevCommit commit1 = allCommits.get(5);
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
    public void getLastTag_ShouldReturnLastWithName() throws IOException {
        final String actual = gitHelper.getLastTag(git, dummyModuleName1).getName();
        final String expected = Constants.R_TAGS + dummyTagName1;

        assertThat(actual, is(expected));
    }

    @Test
    public void getNewCommits_ShouldReturnNew() throws IOException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<RevCommit> actual = gitHelper.getNewCommits(git, lastTag);

        final RevWalk allCommits = gitHelper.getAllCommitsInIterator(git);
        final List<RevCommit> expected = new ArrayList<>();
        expected.add(allCommits.next());
        expected.add(allCommits.next());
        expected.add(allCommits.next());
        expected.add(allCommits.next());

        assertThat(actual, is(expected));
    }

    @Test
    public void getChangedFileList_ShouldContainOneChange() throws IOException, GitAPIException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<RevCommit> newCommits = gitHelper.getNewCommits(git, lastTag);

        final List<DiffEntry> actual = gitHelper.getCommitDiffList(newCommits.get(1).getTree().getId(),
                newCommits.get(2).getTree().getId(), git.getRepository());
        assertThat(actual.size(), is(1));
    }

    @Test
    public void getChangedFileList_ShouldContainAdditionOfDummyFile() throws IOException, GitAPIException {
        final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
        final List<DiffEntry> diffList = gitHelper.getCommitDiffList(allCommits.get(4).getTree().getId(),
                allCommits.get(5).getTree().getId(), git.getRepository());

        final String actual = diffList.get(0).getNewPath();
        assertThat(actual, is(dummyModuleName1 + "/" + dummyFileName2));
    }

    @Test
    public void getChangedFileList_ShouldContainModificationOfDummyFile() throws IOException, GitAPIException {
        final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
        final List<DiffEntry> diffList = gitHelper.getCommitDiffList(allCommits.get(2).getTree().getId(),
                allCommits.get(3).getTree().getId(), git.getRepository());

        final String actual = diffList.get(0).getNewPath();
        assertThat(actual, is(dummyModuleName2 + "/" + dummyFileName3));
    }

    @Test
    public void getChangedFileList_ShouldContainRemovalOfDummyFile() throws IOException, GitAPIException {
        final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
        final List<DiffEntry> diffList = gitHelper.getCommitDiffList(allCommits.get(1).getTree().getId(),
                allCommits.get(2).getTree().getId(), git.getRepository());

        final String actual = diffList.get(0).getOldPath();
        assertThat(actual, is(dummyModuleName1 + "/" + dummyFileName2));
    }

    @Test
    public void isModuleAffected_ShouldNotAffectModule() throws IOException, GitAPIException {
        final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
        final List<DiffEntry> diffList = gitHelper.getCommitDiffList(allCommits.get(4).getTree().getId(),
                allCommits.get(5).getTree().getId(), git.getRepository());

        assertFalse(gitHelper.isModuleAffected(dummyModule2, localDir, diffList));
    }

    @Test
    public void isModuleAffected_ShouldAffectModule() throws IOException, GitAPIException {
        final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
        final List<DiffEntry> diffList = gitHelper.getCommitDiffList(allCommits.get(4).getTree().getId(),
                allCommits.get(5).getTree().getId(), git.getRepository());

        assertTrue(gitHelper.isModuleAffected(dummyModule1, localDir, diffList));
    }

    @Test
    public void isModuleAffected_ShouldAffectBothModule() throws IOException, GitAPIException {
        final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
        final List<DiffEntry> diffList = gitHelper.getCommitDiffList(allCommits.get(0).getTree().getId(),
                allCommits.get(1).getTree().getId(), git.getRepository());

        assertTrue(gitHelper.isModuleAffected(dummyModule1, localDir, diffList));
        assertTrue(gitHelper.isModuleAffected(dummyModule2, localDir, diffList));
    }

    @Test
    public void isModuleParent_ShouldBeTrue() throws IOException {
        assertTrue(gitHelper.isModuleParent(dummyModule2, localDir, dummyModuleName2 + "/" + dummyFileName3));
    }

    @Test
    public void isModuleParent_ShouldBeFalse() throws IOException {
        assertFalse(gitHelper.isModuleParent(dummyModule2, localDir, dummyModuleName1 + "/" + dummyFileName3));
    }

    @Test
    public void isModuleChild_ShouldBeTrue() throws IOException {
        assertTrue(gitHelper.isModuleChild(dummyModule2, localDir, "/" + dummyFileName1));
    }

    @Test
    public void isModuleChild_ShouldBeFalse() throws IOException {
        assertFalse(gitHelper.isModuleChild(dummyModule2, localDir, dummyModuleName1 + "/" + dummyFileName3));
    }
    //endRegion

    // startRegion ChangeLogHelper Tests

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
        assertTrue(actual.startsWith("### module2 - 1.0.0 - "));
    }

    @Test
    public void getReleaseName_ChoreRelease() throws IOException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<UpdateChangeLogTask.ChangeLogEntry> changeLogEntries = Collections.emptyList();

        final String actual = changeLogHelper.getReleaseName(lastTag, changeLogEntries);
        assertTrue(actual.startsWith("### module2 - 0.1.1 -"));
    }

    @Test
    public void getNewVersion_patchVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyVersionName1,
                UpdateChangeLogTask.getPatchCommitTypes());
        assertThat(actual, is("0.0.2"));
    }

    @Test
    public void getNewVersion_minorVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyVersionName1,
                UpdateChangeLogTask.getMinorCommitTypes());
        assertThat(actual, is("0.1.0"));
    }

    @Test
    public void getNewVersion_majorVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyVersionName1,
                UpdateChangeLogTask.getMajorCommitTypes());
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
        assertThat(actual.get(changeLogLines.size() - 3), is(dummyReleaseName));
        assertThat(actual.get(changeLogLines.size() - 2), is(UpdateChangeLogTask.maintenanceReleaseEntry));
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
        assertThat(actual.get(changeLogLines.size() - 4), is(dummyReleaseName));

        assertThat(actual.get(changeLogLines.size() - 3),
                is(changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage4).toString()));
        assertThat(actual.get(changeLogLines.size() - 2),
                is(changeLogHelper.formatCommitToChangeLogEntry(dummyCommitMessage3).toString()));
    }

    @Test
    public void filterRelevantCommits_ShouldListOnlyRelevantCommits() throws IOException, GitAPIException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<RevCommit> newCommits = gitHelper.getNewCommits(git, lastTag);

        final List<RevCommit> actual = gitHelper.filterRelevantCommits(git, newCommits, dummyModule1);
        final RevCommit[] expected = new RevCommit[]{newCommits.get(0), newCommits.get(1)};
        assertThat(actual, containsInAnyOrder(expected));
    }
    //endRegion

    // region InputHelper tests
    @Test
    public void getAvailableModules_ShouldContainAll() {
        final Set<File> availableModules = inputHelper.getAvailableModules(mockRootProject);
        final Set<String> actual = availableModules.stream().map(File::getName).collect(Collectors.toSet());
        final Set<String> expected = new HashSet<>(Arrays.asList(dummyModuleName1, dummyModuleName2));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void getAvailableModules_ShouldBeEmpty() {
        final Set<File> actual = inputHelper.getAvailableModules(mockEmptyProject);
        assertThat(actual, containsInAnyOrder(new HashSet<>().toArray()));
    }

    @Test
    public void validateModules_EmptyShouldNotThrowException() {
        final Set<File> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>());
    }

    @Test
    public void validateModules_PartialMatchShouldNotThrowException() {
        final Set<File> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>(Collections.singletonList(dummyModuleName1)));
    }

    @Test
    public void validateModules_AllMatchShouldNotThrowException() {
        final Set<File> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>(Arrays.asList(dummyModuleName2, dummyModuleName1)));
    }

    @Test(expected = IllegalStateException.class)
    public void validateModules_InvalidNameShouldThrowException() {
        final Set<File> availableModules = inputHelper.getAvailableModules(mockRootProject);
        inputHelper.validateModules(availableModules, new HashSet<>(Arrays.asList(dummyModuleName2,
                "weDoNotHaveThis")));
    }

    @Test
    public void getModuleDirNamesToUpdate_ShouldBeAvailableModules() {
        final Set<File> expected = inputHelper.getAvailableModules(mockRootProject);
        final Set<File> actual = inputHelper.getModuleDirsToUpdate(mockRootProject, new HashSet<>());
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void getModuleDirNamesToUpdate_ShouldBeTheInput() {
        final Set<File> moduleDirsToUpdate = inputHelper.getModuleDirsToUpdate(mockRootProject,
                new HashSet<>(Collections.singletonList(dummyModuleName1)));
        final Set<String> actual = moduleDirsToUpdate.stream().map(File::getName).collect(Collectors.toSet());
        assertThat(actual, containsInAnyOrder(dummyModuleName1));
    }

    @Test
    public void getModuleDirNamesToUpdate_ShouldBeAllInput() {
        final Set<File> moduleDirsToUpdate = inputHelper.getModuleDirsToUpdate(mockRootProject,
                new HashSet<>(Arrays.asList(dummyModuleName1, dummyModuleName2)));
        final Set<String> actual = moduleDirsToUpdate.stream().map(File::getName).collect(Collectors.toSet());
        assertThat(actual, containsInAnyOrder(dummyModuleName1, dummyModuleName2));
    }

    @Test(expected = IllegalStateException.class)
    public void getModuleDirNamesToUpdate_InvalidInputShouldThrowException() {
        inputHelper.getModuleDirsToUpdate(mockRootProject,
                new HashSet<>(Arrays.asList("weDoNotHaveThis", dummyModuleName2)));
    }

    // endRegion
}
