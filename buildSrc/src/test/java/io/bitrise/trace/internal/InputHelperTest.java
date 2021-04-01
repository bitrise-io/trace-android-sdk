package io.bitrise.trace.internal;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Unit tests for {@link InputHelper}.
 */
public class InputHelperTest extends TestParent {
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

}
