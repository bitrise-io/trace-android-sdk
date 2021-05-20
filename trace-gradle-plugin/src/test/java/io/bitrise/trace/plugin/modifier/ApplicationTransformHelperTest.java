package io.bitrise.trace.plugin.modifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Unit tests for {@link ApplicationTransformHelper}.
 */
public class ApplicationTransformHelperTest {

  private static final String DUMMY_STRING_1 = "DUMMY1";
  private static final String DUMMY_STRING_2 = "DUMMY2";
  private static final String DUMMY_STRING_3 = "DUMMY3";
  private static final String DUMMY_EMPTY_STRING = "";

  @Test
  public void filterNullStrings_EmptyShouldReturnEmpty() {
    final Set<String> emptySet = new HashSet<>(Collections.emptyList());
    assertThat(ApplicationTransformHelper.filterNullStrings(emptySet).size(), is(0));
  }

  @Test
  public void filterNullStrings_NonNullsShouldReturnAll() {
    final Set<String> nonNullSet = new HashSet<>(
        Arrays.asList(DUMMY_STRING_1, DUMMY_STRING_2, DUMMY_STRING_3, DUMMY_EMPTY_STRING));
    assertThat(ApplicationTransformHelper.filterNullStrings(nonNullSet).size(), is(4));
  }

  @Test
  public void filterNullStrings_NullsShouldReturnEmpty() {
    final Set<String> nullSet = new HashSet<>(Arrays.asList(null, null, null, null));
    assertThat(ApplicationTransformHelper.filterNullStrings(nullSet).size(), is(0));
  }

  @Test
  public void filterNullStrings_MixedShouldReturnNonNull() {
    final Set<String> mixedSet =
        new HashSet<>(Arrays.asList(null, DUMMY_STRING_1, null, DUMMY_EMPTY_STRING));
    assertThat(ApplicationTransformHelper.filterNullStrings(mixedSet).size(), is(2));
  }
}