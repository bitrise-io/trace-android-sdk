package io.bitrise.trace.scheduler;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler implementation. Uses a {@link Runnable} as the action. It will be will be executed
 * recurring, but stops when the app is terminated.
 *
 * @see
 * <a href="https://developer.android.com/reference/java/util/concurrent/ScheduledThreadPoolExecutor">ScheduledThreadPoolExecutor</a>
 */
public class ExecutorScheduler extends Scheduler {

  /**
   * The default interval for scheduling.
   */
  public static final long DEFAULT_SCHEDULE_INTERVAL_MS = 1000 * 60 * 5;

  /**
   * The interval for scheduling in milliseconds.
   */
  private final long interval;

  /**
   * The Runnable to schedule.
   */
  @Nullable
  @VisibleForTesting
  final Runnable runnable;

  /**
   * The {@link ScheduledExecutorService} that will schedule the {@link Runnable}s.
   */
  @Nullable
  @VisibleForTesting
  ScheduledExecutorService scheduledExecutorService;

  /**
   * The ScheduledFuture that is created when a task is scheduled on the executor.
   */
  @VisibleForTesting
  @Nullable
  ScheduledFuture<?> scheduledFuture;

  /**
   * Constructor for class. {@link #DEFAULT_SCHEDULE_INITIAL_DELAY_MS} will be used as delay
   * for the scheduling.
   *
   * @param context  the Android Context.
   * @param runnable the Runnable to schedule.
   */
  public ExecutorScheduler(@NonNull final Context context, @NonNull final Runnable runnable) {
    this.context = context;
    this.runnable = runnable;
    this.initialDelay = DEFAULT_SCHEDULE_INITIAL_DELAY_MS;
    this.interval = DEFAULT_SCHEDULE_INTERVAL_MS;
  }

  /**
   * Constructor for class.
   *
   * @param context      the Android Context.
   * @param runnable     the Runnable to schedule.
   * @param initialDelay the initial delay for scheduling in milliseconds.
   * @param interval     the interval for the recurring scheduling.
   */
  public ExecutorScheduler(@NonNull final Context context, @NonNull final Runnable runnable,
                           final long initialDelay, final long interval) {
    this.context = context;
    this.runnable = runnable;
    this.initialDelay = initialDelay;
    this.interval = interval;
  }

  @Nullable
  @Override
  @SuppressLint("infer")
  public Integer scheduleDelayed(final long initialDelay) {
    if (runnable == null) {
      return null;
    }
    if (scheduledExecutorService == null) {
      scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }
    if (interval > 0) {
      scheduledFuture = scheduledExecutorService
          .scheduleAtFixedRate(runnable, initialDelay, interval, TimeUnit.MILLISECONDS);
    } else {
      scheduledFuture = scheduledExecutorService.schedule(runnable, initialDelay,
          TimeUnit.MILLISECONDS);
    }

    return -1;
  }

  @Override
  public void cancelAll() {
    if (scheduledExecutorService != null) {
      scheduledExecutorService.shutdownNow();
    }
  }
}
