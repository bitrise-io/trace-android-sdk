package io.bitrise.trace.scheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Base abstract class for Trace JobServices. Work is done on a new thread.
 */
public abstract class ScheduledJobService extends JobService {

    /**
     * The key name for delay values that are passed to this service. Value should be in
     * milliseconds.
     */
    public static final String DELAY_KEY_MS = "delay";

    @Override
    public boolean onStartJob(final JobParameters params) {
        final long delay = params.getExtras().getLong(DELAY_KEY_MS);
        final ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                performJobAction(params);
            }
        }, delay, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * Called when the scheduled action should be executed. When the task is finished it should
     * explicitly call {@link JobService#jobFinished(JobParameters, boolean)}.
     *
     * @param jobParameters the parameters of the job to run.
     */
    public abstract void performJobAction(@NonNull final JobParameters jobParameters);

    /**
     * Gets the ID of the given job. Should be implemented by a subclass.
     *
     * @return the ID of the created job.
     */
    public abstract int getJobId();
}
