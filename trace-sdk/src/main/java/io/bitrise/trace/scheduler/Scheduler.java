package io.bitrise.trace.scheduler;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Base abstract class for scheduling actions.
 */
public abstract class Scheduler {

  /**
   * The default initial delay for scheduling in milliseconds.
   */
  public static final long DEFAULT_SCHEDULE_INITIAL_DELAY_MS = 60 * 1000;

  /**
   * The Android context.
   */
  @NonNull
  Context context;

  /**
   * The initial delay for the Scheduler.
   */
  long initialDelay;

  /**
   * Schedules an immediate action.
   *
   * @return the ID of the job, or {@code null} if the action hasn't been scheduled.
   */
  @Nullable
  public Integer schedule() {
    return schedule(0);
  }

  /**
   * Schedules an action to the given timestamp.
   *
   * @param scheduledTime the exact time when the callback will be called. If the given time is
   *                      already passed the action will be triggered immediately.
   * @return the ID of if it is a Service, negative number if it is not a Service, or {@code
   * null}if the action has not been scheduled.
   */
  @Nullable
  public Integer schedule(final long scheduledTime) {
    final long delay = calculateDelay(scheduledTime);
    return scheduleDelayed(delay);
  }

  /**
   * Schedules an action delayed with the default delay.
   *
   * @return the tag of if it is a Service, empty String if it is not a Service,  or {@code null}
   * if the action has not been scheduled.
   */
  @Nullable
  public Integer scheduleDelayed() {
    return scheduleDelayed(initialDelay);
  }

  /**
   * Schedules an action delayed.
   *
   * @param delay the amount of delay in milliseconds.
   * @return the ID of if it is a Service, negative number if it is not a Service, or {@code null}
   * if the action has not been scheduled.
   */
  @Nullable
  public abstract Integer scheduleDelayed(final long delay);

  /**
   * Cancels all the scheduled actions.
   */
  public abstract void cancelAll();

  /**
   * Calculates the delay to the given time. If the given times has already passed, it returns 0.
   *
   * @param scheduledTimeMillis the timestamp in unix-time milliseconds.
   * @return the delay in milliseconds.
   */
  private long calculateDelay(final long scheduledTimeMillis) {
    final long currentTime = System.currentTimeMillis();
    final long diff = scheduledTimeMillis - currentTime;
    return diff > 0 ? diff : 0;
  }
}
