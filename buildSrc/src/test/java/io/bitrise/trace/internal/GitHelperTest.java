package io.bitrise.trace.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.Test;

/**
 * Unit tests for the {@link GitHelper} class.
 */
public class GitHelperTest extends TestParent {

  @Test
  public void getMajorCommitTypes_ShouldReturnExpectedValues() {
    final Set<String> actual = ChangeLogHelper.getMajorCommitTypes();
    final Set<String> expected = new HashSet<>(Arrays.asList("fix!", "feat!"));
    assertEquals(actual, expected);
  }

  @Test
  public void getMinorCommitTypes_ShouldReturnExpectedValues() {
    final Set<String> actual = ChangeLogHelper.getMinorCommitTypes();
    final Set<String> expected = new HashSet<>(Collections.singletonList("feat"));
    assertEquals(actual, expected);
  }

  @Test
  public void getPatchCommitTypes_ShouldReturnExpectedValues() {
    final Set<String> actual = ChangeLogHelper.getPatchCommitTypes();
    final Set<String> expected = new HashSet<>(Collections.singletonList("fix"));
    assertEquals(actual, expected);
  }

  @Test
  public void getAllowedCommitTypes_ShouldReturnExpectedValues() {
    final Set<String> actual = ChangeLogHelper.getAllowedCommitTypes();
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
  public void getLastTag_ShouldReturnNull() throws IOException {
    final Ref actual = gitHelper.getLastTag(git, dummyInvalidModuleName);
    assertThat(actual, is(nullValue()));
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
  public void getChangedFileList_ShouldContainAdditionOfDummyFile()
      throws IOException, GitAPIException {
    final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
    final List<DiffEntry> diffList =
        gitHelper.getCommitDiffList(allCommits.get(4).getTree().getId(),
            allCommits.get(5).getTree().getId(), git.getRepository());

    final String actual = diffList.get(0).getNewPath();
    assertThat(actual, is(dummyModuleName1 + "/" + dummyFileName2));
  }

  @Test
  public void getChangedFileList_ShouldContainModificationOfDummyFile()
      throws IOException, GitAPIException {
    final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
    final List<DiffEntry> diffList =
        gitHelper.getCommitDiffList(allCommits.get(2).getTree().getId(),
            allCommits.get(3).getTree().getId(), git.getRepository());

    final String actual = diffList.get(0).getNewPath();
    assertThat(actual, is(dummyModuleName2 + "/" + dummyFileName3));
  }

  @Test
  public void getChangedFileList_ShouldContainRemovalOfDummyFile()
      throws IOException, GitAPIException {
    final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
    final List<DiffEntry> diffList =
        gitHelper.getCommitDiffList(allCommits.get(1).getTree().getId(),
            allCommits.get(2).getTree().getId(), git.getRepository());

    final String actual = diffList.get(0).getOldPath();
    assertThat(actual, is(dummyModuleName1 + "/" + dummyFileName2));
  }

  @Test
  public void isModuleAffected_ShouldNotAffectModule() throws IOException, GitAPIException {
    final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
    final List<DiffEntry> diffList =
        gitHelper.getCommitDiffList(allCommits.get(4).getTree().getId(),
            allCommits.get(5).getTree().getId(), git.getRepository());

    assertFalse(gitHelper.isModuleAffected(dummyModule2, localDir, diffList));
  }

  @Test
  public void isModuleAffected_ShouldAffectModule() throws IOException, GitAPIException {
    final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
    final List<DiffEntry> diffList =
        gitHelper.getCommitDiffList(allCommits.get(4).getTree().getId(),
            allCommits.get(5).getTree().getId(), git.getRepository());

    assertTrue(gitHelper.isModuleAffected(dummyModule1, localDir, diffList));
  }

  @Test
  public void isModuleAffected_ShouldAffectBothModule() throws IOException, GitAPIException {
    final List<RevCommit> allCommits = gitHelper.getAllCommits(git);
    final List<DiffEntry> diffList =
        gitHelper.getCommitDiffList(allCommits.get(0).getTree().getId(),
            allCommits.get(1).getTree().getId(), git.getRepository());

    assertTrue(gitHelper.isModuleAffected(dummyModule1, localDir, diffList));
    assertTrue(gitHelper.isModuleAffected(dummyModule2, localDir, diffList));
  }

  @Test
  public void isModuleParent_ShouldBeTrue() throws IOException {
    assertTrue(
        gitHelper.isModuleParent(dummyModule2, localDir, dummyModuleName2 + "/" + dummyFileName3));
  }

  @Test
  public void isModuleParent_ShouldBeFalse() throws IOException {
    assertFalse(
        gitHelper.isModuleParent(dummyModule2, localDir, dummyModuleName1 + "/" + dummyFileName3));
  }

  @Test
  public void isModuleChild_ShouldBeTrue() throws IOException {
    assertTrue(gitHelper.isModuleChild(dummyModule2, localDir, "/" + dummyFileName1));
  }

  @Test
  public void isModuleChild_ShouldBeFalse() throws IOException {
    assertFalse(
        gitHelper.isModuleChild(dummyModule2, localDir, dummyModuleName1 + "/" + dummyFileName3));
  }
}
