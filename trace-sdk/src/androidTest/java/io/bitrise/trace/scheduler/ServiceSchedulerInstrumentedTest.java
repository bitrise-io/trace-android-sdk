package io.bitrise.trace.scheduler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import io.bitrise.trace.network.MetricSender;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Instrumentation tests for {@link ServiceScheduler}.
 */
public class ServiceSchedulerInstrumentedTest {

  private final Context mockContext = Mockito.mock(Context.class);
  final JobScheduler mockJobScheduler = Mockito.mock(JobScheduler.class);

  @Test
  public void scheduleDelay() {
    long initialDelay = 1234L;
    final ServiceScheduler scheduler = new ServiceScheduler(mockContext, MetricSender.class);
    scheduler.componentName = new ComponentName("test", "test");
    when(mockContext.getSystemService(Context.JOB_SCHEDULER_SERVICE))
        .thenReturn(mockJobScheduler);

    scheduler.scheduleDelayed(initialDelay);

    ArgumentCaptor<JobInfo> jobInfoCaptor = ArgumentCaptor.forClass(JobInfo.class);
    verify(mockJobScheduler, times(1)).schedule(jobInfoCaptor.capture());
    assertEquals(initialDelay, jobInfoCaptor.getValue().getMinLatencyMillis());
  }

}
