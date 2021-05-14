package io.bitrise.trace.scheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.network.MetricSender;
import io.bitrise.trace.network.TraceSender;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;

/**
 * Scheduler implementation. Uses a subclass of the {@link android.app.job.JobScheduler} as the
 * action. The jobs will run by the a {@link android.app.job.JobScheduler}. Service will be
 * executed even when the app is terminated. For details of the JobScheduler please check
 * official docs.
 *
 * @see
 * <a href="https://developer.android.com/reference/android/app/job/JobScheduler">JobScheduler</a>
 */
public class ServiceScheduler extends Scheduler {

  /**
   * The class of a {@link JobService} that should be created.
   */
  @NonNull
  private final Class<? extends JobService> jobService;

  /**
   * Constructor for class. {@link #DEFAULT_SCHEDULE_INITIAL_DELAY_MS} will be used as delay
   * for the scheduling.
   *
   * @param context    the Android Context.
   * @param jobService the class of the JobService to schedule.
   */
  public ServiceScheduler(@NonNull final Context context,
                          @NonNull final Class<? extends JobService> jobService) {
    this.context = context;
    this.jobService = jobService;
    this.initialDelay = DEFAULT_SCHEDULE_INITIAL_DELAY_MS;
  }

  /**
   * Constructor for class.
   *
   * @param context    the Android Context.
   * @param jobService the class of the Worker to schedule.
   * @param delay      the delay for scheduling in milliseconds.
   */
  public ServiceScheduler(@NonNull final Context context,
                          @NonNull final Class<? extends JobService> jobService,
                          final long delay) {
    this.context = context;
    this.jobService = jobService;
    this.initialDelay = delay;
  }

  @Override
  @Nullable
  public Integer scheduleDelayed(final long initialDelay) {
    final JobInfo jobInfo = new JobInfo.Builder(getJobIdForService(jobService),
        new ComponentName(context, jobService))
        .setBackoffCriteria(18000, JobInfo.BACKOFF_POLICY_LINEAR)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        .setMinimumLatency(initialDelay)
        .build();

    final JobScheduler jobScheduler =
        (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    TraceLog.d(String.format(LogMessageConstants.SCHEDULER_SEND_SCHEDULE_DELAYED, initialDelay));
    return jobScheduler.schedule(jobInfo);
  }

  @Override
  public void cancelAll() {
    ((JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE)).cancelAll();
    TraceLog.d(LogMessageConstants.SCHEDULER_SEND_CANCELLED);
  }

  /**
   * Cancels all the scheduled Service with the given tag.
   *
   * @param id the ID of the Service to cancel.
   */
  public void cancelServiceByTag(@NonNull final int id) {
    ((JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE)).cancel(id);
  }

  /**
   * Gets the ID for the given {@link JobService} task.
   *
   * @param jobService the given JobService.
   * @return the ID for the given JobService, IllegalArgumentException if it is not a
   * {@link MetricSender} or
   * {@link TraceSender}.
   */
  private int getJobIdForService(@NonNull final Class<? extends JobService> jobService) {
    if (jobService == MetricSender.class) {
      return 0;
    } else if (jobService == TraceSender.class) {
      return 1;
    }
    throw new IllegalArgumentException(
        "Could not determine the ID for class " + jobService.getName());
  }
}
