package io.bitrise.trace.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

import com.google.protobuf.Timestamp;
import java.util.TimeZone;
import org.junit.Test;

/**
 * Test cases for {@link TraceClock}.
 */
public class TraceClockTest {

  final long dummyMilliseconds1 = 1000;
  final long dummyMilliseconds2 = 1999;
  final long dummyMilliseconds3 = 1603374824000L;
  final long dummyMilliseconds4 = 1628173584882L;

  /**
   * Check if the result of a milliseconds value is divided by 1000.
   */
  @Test
  public void getTimestampSeconds_ShouldDivide() {
    assertThat(TraceClock.getSeconds(dummyMilliseconds1), is(1L));
  }

  /**
   * Check if the result of a milliseconds is rounded down.
   */
  @Test
  public void getTimestampSeconds_ShouldRoundDown() {
    assertThat(TraceClock.getSeconds(dummyMilliseconds2), is(1L));
  }

  /**
   * Check if a millisecond value that is a round value (for seconds), the nanos should be 0.
   */
  @Test
  public void getTimestampNanos_ShouldBeZero() {
    assertThat(TraceClock.getNanos(dummyMilliseconds1), is(0));
  }

  /**
   * Checks if the result of a milliseconds value is multiplied by 1000.
   */
  @Test
  public void getTimestampNanos_ShouldMultiply() {
    assertThat(TraceClock.getNanos(dummyMilliseconds2), is(999000000));
  }

  /**
   * Checks that the method {@link TraceClock#createTimestamp(long)} with
   * {@link #dummyMilliseconds1} will return a correctly formatted {@link Timestamp}.
   */
  @Test
  public void getTimestamp_SecondsAndNanosShouldBeCorrect1() {
    final Timestamp expectedValue = Timestamp.newBuilder().setSeconds(1L).setNanos(0).build();
    final Timestamp actualValue = TraceClock.createTimestamp(dummyMilliseconds1);
    assertThat(actualValue, is(equalTo(expectedValue)));
  }

  /**
   * Checks that the method {@link TraceClock#createTimestamp(long)} with
   * {@link #dummyMilliseconds2} will return a correctly formatted {@link Timestamp}.
   */
  @Test
  public void getTimestamp_SecondsAndNanosShouldBeCorrect2() {
    final Timestamp expectedValue =
        Timestamp.newBuilder().setSeconds(1L).setNanos(999000000).build();
    final Timestamp actualValue = TraceClock.createTimestamp(dummyMilliseconds2);
    assertThat(actualValue, is(equalTo(expectedValue)));
  }

  /**
   * Checks that the method {@link TraceClock#createTimestamp(long)} with
   * {@link #dummyMilliseconds3} will return a correctly formatted {@link Timestamp}.
   */
  @Test
  public void getTimestamp_SecondsAndNanosShouldBeCorrect3() {
    final Timestamp expectedValue =
        Timestamp.newBuilder().setSeconds(1603374824L).setNanos(0).build();
    final Timestamp actualValue = TraceClock.createTimestamp(dummyMilliseconds3);
    assertThat(actualValue, is(equalTo(expectedValue)));
  }

  @Test
  public void getTimeStamp_convertsBothWaysWithRealDate() {
    final Timestamp expectedTimestamp =
        Timestamp.newBuilder().setSeconds(1628173584L).setNanos(882000000).build();
    assertEquals(expectedTimestamp, TraceClock.createTimestamp(dummyMilliseconds4));
    assertEquals(dummyMilliseconds4, TraceClock.timestampToMillis(expectedTimestamp));
  }

  @Test
  public void createCrashRequestFormat() {
    assertEquals("2020-10-22T14:53:44+01:00", TraceClock
            .createCrashRequestFormat(dummyMilliseconds3, TimeZone.getTimeZone("Europe/London")));
  }
}