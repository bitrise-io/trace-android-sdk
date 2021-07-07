package io.bitrise.trace.utils;

import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.google.protobuf.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides the time for different components of Trace SDK.
 */
public class TraceClock {

  private TraceClock() {
    throw new UnsupportedOperationException("Private constructor for class!");
  }

  /**
   * Gets the seconds from a time value in milliseconds.
   *
   * @param timeInMilliseconds the time in milliseconds.
   * @return the seconds of the given time.
   */
  public static long getSeconds(final long timeInMilliseconds) {
    return timeInMilliseconds / 1000;
  }

  /**
   * Gets the nanoseconds from a time value in milliseconds.
   *
   * <p>Example: from 300123 value (milliseconds) it will get the 123 and multiply it with 1 000
   * 000, resulting in 123 000 000 (nanoseconds).
   *
   * @param timeInMilliseconds the time in milliseconds.
   * @return the nanoseconds of the given time.
   */
  public static int getNanos(final long timeInMilliseconds) {
    final long milliseconds = timeInMilliseconds % 1000;
    return (int) milliseconds * 1000 * 1000;
  }

  /**
   * Gets the current {@link Timestamp}.
   *
   * @return the current Timestamp.
   */
  @NonNull
  public static Timestamp getCurrentTimestamp() {
    return createTimestamp(getCurrentTimeMillis());
  }

  /**
   * Creates a {@link Timestamp} from the given time in milliseconds.
   *
   * @param timeInMilliseconds the time in milliseconds.
   * @return the created Timestamp.
   */
  @NonNull
  public static Timestamp createTimestamp(final long timeInMilliseconds) {
    return Timestamp.newBuilder()
                    .setSeconds(getSeconds(timeInMilliseconds))
                    .setNanos(getNanos(timeInMilliseconds))
                    .build();
  }

  /**
   * Gets the current time in milliseconds.
   *
   * @return the current time in milliseconds.
   */
  public static long getCurrentTimeMillis() {
    // TODO this has to be revised
    return System.currentTimeMillis();
  }

  /**
   * Gets the uptime of the system in seconds.
   *
   * @return the uptime in seconds.
   */
  @VisibleForTesting
  public static long getElapsedSeconds() {
    return SystemClock.uptimeMillis() / 1000;
  }

  /**
   * Formats a given millisecond into the format expected by the backend for a crash report.
   *
   * @param milliseconds the current timestamp in milliseconds.
   * @return the String representation of the timestamp for crash reports.
   */
  public static String createCrashRequestFormat(final long milliseconds,
                                                final TimeZone timeZone) {
    final SimpleDateFormat sdf =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
    sdf.setTimeZone(timeZone);

    final String date = sdf.format(new Date(milliseconds));

    // we need to add a colon in between to make +0100 into +01:00
    return date.substring(0, date.length() - 2) + ":" + ""
        + date.substring(date.length() - 2);
  }
}
