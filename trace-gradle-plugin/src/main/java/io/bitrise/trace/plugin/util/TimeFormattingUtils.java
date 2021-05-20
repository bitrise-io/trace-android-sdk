package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Formats the time to the required format. Use this to avoid having multiple formats.
 */
public class TimeFormattingUtils {

  private TimeFormattingUtils() {
    throw new IllegalStateException(
        "Should not be instantiated, used only for storing static members!");
  }

  /**
   * Formats a given Data to a readable format with the given TimeZone.
   *
   * @param date     the Date to format.
   * @param timeZone the TimeZone to format with.
   * @return the formatted Date.
   */
  @NonNull
  public static String formatTime(@NonNull final Date date, @NonNull final TimeZone timeZone) {
    final SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss, EEE, Z", Locale.ENGLISH);
    simpleDateFormat.setTimeZone(timeZone);
    return simpleDateFormat.format(date);
  }

  /**
   * Formats a given Data to a readable format with the default TimeZone.
   *
   * @param date the Date to format.
   * @return the formatted Date.
   */
  @NonNull
  public static String formatTime(@NonNull final Date date) {
    return formatTime(date, TimeZone.getDefault());
  }
}
