package io.bitrise.trace.plugin.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;

/**
 * Contains tests for {@link TimeFormattingUtils} class.
 */
public class TimeFormattingUtilsTest {

  /**
   * Checks that the formatted value is equal to what we expect from it to provide.
   */
  @Test
  public void testFormatTime_withCustomTimeZone() {
    final Date date = new Date(0);
    final String expectedValue = "1970-01-01, 05:00:00, Thu, +0500";
    final String actualValue = TimeFormattingUtils.formatTime(date, TimeZone.getTimeZone("GMT+5"));
    assertThat(actualValue, equalTo(expectedValue));
  }
}