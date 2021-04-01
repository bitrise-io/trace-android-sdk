package io.bitrise.trace.internal;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.gradle.api.Project;
import org.gradle.api.initialization.IncludedBuild;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Abstract parent class for test classes.
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
public abstract class TestParent {

    protected static final String dummyCommitTemplate = "%s: %s\n\n%s\n\n%s";
    protected static final String dummyCommitType1 = "fix";
    protected static final String dummyCommitTitle1 = "Title 1. Some fix";
    protected static final String dummyCommitDetails1 = "This was needed.";
    protected static final String dummyCommitFooter1 = "APM-12345";
    protected static final String dummyCommitMessage1 = String.format(dummyCommitTemplate, dummyCommitType1,
            dummyCommitTitle1, dummyCommitDetails1, dummyCommitFooter1);
    protected static final String dummyCommitType2 = "feat";
    protected static final String dummyCommitTitle2 = "Title 2. Some feature";
    protected static final String dummyCommitDetails2 = "This was needed.\nAdded cool new features";
    protected static final String dummyCommitFooter2 = "APM-23456";
    protected static final String dummyCommitMessage2 = String.format(dummyCommitTemplate, dummyCommitType2,
            dummyCommitTitle2, dummyCommitDetails2, dummyCommitFooter2);
    protected static final String dummyCommitType3 = "fix";
    protected static final String dummyCommitTitle3 = "Title 3. Some other fix";
    protected static final String dummyCommitDetails3 = "This was also needed. Modified previous file!";
    protected static final String dummyCommitFooter3 = "APM-34567";
    protected static final String dummyCommitMessage3 = String.format(dummyCommitTemplate, dummyCommitType3,
            dummyCommitTitle3, dummyCommitDetails3, dummyCommitFooter3);
    protected static final String dummyCommitType4 = "feat!";
    protected static final String dummyCommitTitle4 = "Title 4. Breaking change";
    protected static final String dummyCommitDetails4 = "API break, new API enables everything!\nUse it " +
            "wisely!\nDeprecated some things. Removed file!";
    protected static final String dummyCommitFooter4 = "APM-45678";
    protected static final String dummyCommitMessage4 = String.format(dummyCommitTemplate, dummyCommitType4,
            dummyCommitTitle4, dummyCommitDetails4, dummyCommitFooter4);
    protected static final String dummyCommitType5 = "chore";
    protected static final String dummyCommitTitle5 = "Title 5. CI update";
    protected static final String dummyCommitDetails5 = "New bitrise.yml";
    protected static final String dummyCommitFooter5 = "APM-56789";
    protected static final String dummyCommitMessage5 = String.format(dummyCommitTemplate, dummyCommitType5,
            dummyCommitTitle5, dummyCommitDetails5, dummyCommitFooter5);
    protected static final String dummyCommitType6 = "test";
    protected static final String dummyCommitTitle6 = "Title 6. Add test";
    protected static final String dummyCommitDetails6 = "Some new test case added.";
    protected static final String dummyCommitFooter6 = "APM-67890";
    protected static final String dummyCommitMessage6 = String.format(dummyCommitTemplate, dummyCommitType6,
            dummyCommitTitle6, dummyCommitDetails6, dummyCommitFooter6);

    protected static final String dummyModuleName1 = "module1";
    protected static final String dummyVersionName1 = "0.0.1";
    protected static final String dummyModuleName2 = "module2";
    protected static final String dummyVersionName2 = "0.1.0";
    protected static final String dummyTagName1 = dummyModuleName1 + "_" + dummyVersionName1;
    protected static final String dummyTagName2 = dummyModuleName2 + "_" + dummyVersionName2;
    protected static final String dummyFileName1 = "something1.txt";
    protected static final String dummyFileName2 = "something2.txt";
    protected static final String dummyInvalidModuleName = "weDoNotHaveThis";

    protected static final Project mockRootProject = mock(Project.class);
    protected static final Gradle mockGradle = mock(Gradle.class);

    protected static final Project mockProject1 = mock(Project.class);
    protected static final IncludedBuild mockProject2 = mock(IncludedBuild.class);

    protected static final Project mockEmptyProject = mock(Project.class);
    protected static final Gradle mockEmptyGradle = mock(Gradle.class);
    protected static final String dummyFileName3 = "something3.txt";
    protected static final String dummyFileName4 = "something4.txt";
    /**
     * A temporary folder to create a Git repo for the tests.
     */
    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();
    /**
     * A folder for the local repo.
     */
    protected static File localDir;
    /**
     * A folder for the remote repo.
     */
    protected static File remoteDir;
    /**
     * The {@link Git} that will be used for testing
     */
    protected static Git git;
    protected static File dummyModule1;
    protected static File dummyModule2;
    protected final ChangeLogHelper changeLogHelper = new ChangeLogHelper(
            Logging.getLogger(TestParent.class.getName()));
    protected final String dummyReleaseName = "Dummy release";
    protected final List<String> changeLogLines = new ArrayList<String>() {{
        add("CHANGES");
        add("=======");
        add("");
        add("trace-android-sdk public beta versions");
        add("--------------------------------------");
        add("**Note:** these versions of the *trace-android-sdk* are stored in a public repo, but should be");
        add("considered still as beta.");
        add("");
    }};
    protected final Logger dummyLogger = Logging.getLogger(TestParent.class.getName());
    protected final GitHelper gitHelper = new GitHelper(dummyLogger);
    protected final InputHelper inputHelper = new InputHelper(dummyLogger);

    /**
     * Initialises a a Git repo in the {@link #tempFolder}. Creates different folders for the local and the remote of
     * the repository.
     *
     * @throws GitAPIException if any Git call fails.
     * @throws IOException     if any I/O error occurs.
     */
    protected static void initDummyRepo() throws GitAPIException, IOException {
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
    protected static void closeDummyRepo() {
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
    protected static boolean addDummyFile(final String moduleName, final String name) throws IOException {
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
    protected static boolean removeDummyFile(final String moduleName, final String name) {
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
    protected static void modifyDummyFile(final String moduleName, final String name) throws IOException {
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
    protected static void commitAndPush(final String commitMessage) throws GitAPIException {
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
    protected static void setupDummyRepo() throws GitAPIException, IOException {
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
    protected static void tag(final String tagName) throws GitAPIException {
        git.tag().setName(tagName).setForceUpdate(true).call();
        git.push().setPushTags().call();
    }

    protected static void setupMockProjects() {
        when(mockRootProject.getAllprojects()).thenReturn(new HashSet<>(Collections.singletonList(mockProject1)));
        when(mockEmptyProject.getAllprojects()).thenReturn(new HashSet<>());
        when(mockProject1.getProjectDir()).thenReturn(new File(tempFolder.getRoot(), dummyModuleName1));
        when(mockProject2.getProjectDir()).thenReturn(new File(tempFolder.getRoot(), dummyModuleName2));

        when(mockRootProject.getGradle()).thenReturn(mockGradle);
        when(mockGradle.getIncludedBuilds()).thenReturn(new HashSet<>(Collections.singletonList(mockProject2)));

        when(mockEmptyProject.getGradle()).thenReturn(mockEmptyGradle);
        when(mockEmptyGradle.getIncludedBuilds()).thenReturn(new HashSet<>(Collections.emptyList()));
    }
}
