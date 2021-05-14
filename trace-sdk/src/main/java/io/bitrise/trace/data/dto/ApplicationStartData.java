package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;

/**
 * Data class for Application launch data.
 */
public class ApplicationStartData {

  private long duration;

  /**
   * The {@link ApplicationStartType} of this launch.
   */
  @NonNull
  private ApplicationStartType applicationStartType;

  /**
   * Constructor for class.
   *
   * @param duration             the duration of the start time.
   * @param applicationStartType the {@link ApplicationStartType} of this launch.
   */
  public ApplicationStartData(final long duration,
                              @NonNull final ApplicationStartType applicationStartType) {
    this.duration = duration;
    this.applicationStartType = applicationStartType;
  }

  public long getDuration() {
    return duration;
  }

  @NonNull
  public ApplicationStartType getApplicationStartType() {
    return applicationStartType;
  }
}
