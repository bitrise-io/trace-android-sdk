package io.bitrise.trace.plugin.configuration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Contains test cases for {@link Version} class.
 */
public class VersionTest {

  /**
   * If the given Version is the same as the other the result should be 0.
   */
  @Test
  public void compareTo_sameVersion() {
    final Version version = new Version("2.0.0");
    final int actualValue = version.compareTo(new Version("2.0.0"));
    assertThat(actualValue, equalTo(0));
  }

  /**
   * If the given Version is newer than the other, the result should be greater than 1.
   */
  @Test
  public void compareTo_olderVersion() {
    final Version version = new Version("2.0.0");
    final int actualValue = version.compareTo(new Version("1.0.0"));
    assertThat(actualValue, greaterThan(0));
  }

  /**
   * If the given Version is older than the other, the result should br less than 0.
   */
  @Test
  public void compareTo_newerVersion() {
    final Version version = new Version("2.0.0");
    final int actualValue = version.compareTo(new Version("2.0.1"));
    assertThat(actualValue, lessThan(0));
  }


  /**
   * If the given Version is the same as the supported it should be ok.
   */
  @Test
  public void isSupported_sameSupportedVersion() {
    final Version version = new Version("2.0.0");
    assertTrue(version.isSupported("2.1.9"));
  }

  /**
   * If the given Version is older than the supported it should not be ok.
   */
  @Test
  public void isSupported_newerSupportedVersion() {
    final Version version = new Version("2.0.0");
    assertFalse(version.isSupported("3.1.12"));
  }

  /**
   * If the given Version is newer than the supported it should not be ok.
   */
  @Test
  public void isSupported_olderSupportedVersion() {
    final Version version = new Version("2.0.0");
    assertFalse(version.isSupported("1.9.9"));
  }
}