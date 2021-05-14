package io.bitrise.trace.utils;

import androidx.annotation.NonNull;
import java.util.List;

/**
 * Utility class for String related operations.
 */
public class StringUtils {

  private StringUtils() {
    throw new UnsupportedOperationException("Private constructor for class!");
  }

  /**
   * Utility method to join strings with a separator.
   *
   * @param arr       the array of strings to join eg. ["one", "two", "three].
   * @param separator the character to separate each string entity e.g. ",". If you do not
   *                  want a separator use "" (empty quotes).
   * @return the resulting string e.g. "one,two,three".
   */
  @NonNull
  public static String join(@NonNull final String[] arr, @NonNull final String separator) {
    if (arr.length == 0) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    sb.append(arr[0]);

    for (int i = 1; i < arr.length; i++) {
      sb.append(separator).append(arr[i]);
    }

    return sb.toString();
  }

  /**
   * Utility method to join strings with a separator.
   *
   * @param arr       the List of strings to join.
   * @param separator the character to separate each string entity e.g. ",". If you do not
   *                  want a separator use "" (empty quotes).
   * @return the resulting string e.g. "one,two,three".
   */
  @NonNull
  public static String join(@NonNull final List<String> arr, @NonNull final String separator) {
    return join(arr.toArray(new String[0]), separator);
  }
}
