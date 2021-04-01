package io.bitrise.trace.internal;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link ChangeLogHelper}.
 */
public class ChangeLogHelperTest extends TestParent {

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
        final List<ChangeLogEntry> changeLogEntries =
                changeLogHelper.getChangeLogEntries(newCommits);

        final List<String> actual = changeLogEntries.stream().map(ChangeLogEntry::toString).collect(
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
        final List<ChangeLogEntry> changeLogEntries =
                changeLogHelper.getChangeLogEntries(newCommits);

        final String actual = changeLogHelper.getReleaseName(lastTag, changeLogEntries);
        assertTrue(actual.startsWith("### module2 - 1.0.0 - "));
    }

    @Test
    public void getReleaseName_ChoreRelease() throws IOException {
        final Ref lastTag = gitHelper.getLastTag(git);
        final List<ChangeLogEntry> changeLogEntries = Collections.emptyList();

        final String actual = changeLogHelper.getReleaseName(lastTag, changeLogEntries);
        assertTrue(actual.startsWith("### module2 - 0.1.1 -"));
    }

    @Test
    public void getNewVersion_patchVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyVersionName1, ChangeLogHelper.getPatchCommitTypes());
        assertThat(actual, is("0.0.2"));
    }

    @Test
    public void getNewVersion_minorVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyVersionName1, ChangeLogHelper.getMinorCommitTypes());
        assertThat(actual, is("0.1.0"));
    }

    @Test
    public void getNewVersion_majorVersionIncrease() {
        final String actual = changeLogHelper.getNewVersion(dummyVersionName1, ChangeLogHelper.getMajorCommitTypes());
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
        final List<ChangeLogEntry> changeLogEntries =
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
}
