package io.bitrise.trace.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * Inner class that holds helper methods for Git related actions.
 */
class GitHelper {

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
   * Gets the {@link Git} to work with (this repo).
   *
   * @param projectDir the directory of the {@link Project}.
   * @return this Git.
   * @throws IOException if any I/O error occurs.
   */
  Git getGit(final File projectDir) throws IOException {
    final Git git = Git.open(new File(projectDir, ".git"));
    git.checkout();
    return git;
  }

  /**
   * Creates a {@link RevWalk} that contains all the commits on this branch.
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
   * Gets the last tag (which was created the latest), which contains the given name.
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
   * Filters a list of commits which affected the given module.
   *
   * @param git        the {@link Git} of the commits.
   * @param newCommits the {@link RevCommit}s that should be filtered.
   * @param moduleDir  the directory  of the given module.
   * @return a filtered List of the commits.
   * @throws IOException     if any I/O error occurs.
   * @throws GitAPIException if any Git call fails.
   */
  List<RevCommit> filterRelevantCommits(final Git git, final List<RevCommit> newCommits,
                                        final File moduleDir)
      throws IOException, GitAPIException {
    final Repository repository = git.getRepository();
    final List<RevCommit> commitsForDiff = addPreviousCommit(git, newCommits);
    final List<ObjectId> objectIdList = commitsForDiff.stream()
                                                      .map(it -> it.getTree().getId())
                                                      .collect(Collectors.toList());

    final List<RevCommit> filteredCommits = new ArrayList<>();
    for (int i = 0; i < objectIdList.size() - 1; i++) {
      final List<DiffEntry> diffEntryList =
          getCommitDiffList(objectIdList.get(i), objectIdList.get(i + 1),
              repository);

      if (isModuleAffected(moduleDir, git.getRepository().getWorkTree(), diffEntryList)) {
        filteredCommits.add(commitsForDiff.get(i));
      }
    }

    return filteredCommits;
  }

  /**
   * Adds the previous commit to the given List of commits.
   *
   * @param git     the {@link Git}.
   * @param commits the List of commits.
   * @return the List containing the previous commit.
   * @throws IOException if any I/O error occurs.
   */
  List<RevCommit> addPreviousCommit(final Git git, final List<RevCommit> commits)
      throws IOException {
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
  boolean isModuleAffected(final File moduleDir, final File rootDir,
                           final List<DiffEntry> diffEntryList)
      throws IOException {
    for (final DiffEntry diffEntry : diffEntryList) {
      final String oldPath = diffEntry.getOldPath();
      final String newPath = diffEntry.getNewPath();

      if (isModuleParent(moduleDir, rootDir, oldPath)
          || isModuleParent(moduleDir, rootDir, newPath)) {
        return true;
      }

      if (isModuleChild(moduleDir, rootDir, oldPath) || isModuleChild(moduleDir, rootDir,
          newPath)) {
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
  boolean isModuleParent(final File moduleDir, final File rootDir, final String path)
      throws IOException {
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
  boolean isModuleChild(final File moduleDir, final File rootDir, final String path)
      throws IOException {
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
                                    final Repository repository)
      throws IOException, GitAPIException {
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
