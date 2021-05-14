package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;

/**
 * Enum for the different Application start types.
 *
 * @see
 * <a href="https://developer.android.com/topic/performance/vitals/launch-time">https://developer.android.com/topic/performance/vitals/launch-time</a>
 */
public enum ApplicationStartType {
  COLD("cold"),
  WARM("warm"),
  ;

  @NonNull
  private String name;

  ApplicationStartType(@NonNull final String name) {
    this.name = name;
  }

  @NonNull
  public String getName() {
    return name;
  }
}