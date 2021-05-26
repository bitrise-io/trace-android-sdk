package io.bitrise.trace.scheduler;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import io.bitrise.trace.network.MetricSender;
import io.bitrise.trace.network.TraceSender;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ServiceScheduler}.
 */
public class ServiceSchedulerTest {

  private final Context mockContext = Mockito.mock(Context.class);
  final JobScheduler mockJobScheduler = Mockito.mock(JobScheduler.class);

  @Test
  public void cancelAll() {
    final ServiceScheduler scheduler = new ServiceScheduler(mockContext, MetricSender.class);
    when(mockContext.getSystemService(Context.JOB_SCHEDULER_SERVICE))
        .thenReturn(mockJobScheduler);

    scheduler.cancelAll();
    verify(mockJobScheduler, times(1)).cancelAll();
  }

  @Test
  public void cancelServiceByTag() {
    final int tagId = 987;
    final ServiceScheduler scheduler = new ServiceScheduler(mockContext, MetricSender.class);
    when(mockContext.getSystemService(Context.JOB_SCHEDULER_SERVICE))
        .thenReturn(mockJobScheduler);

    scheduler.cancelServiceByTag(tagId);
    verify(mockJobScheduler, times(1)).cancel(tagId);
  }

  @Test
  public void getJobIdForService_metricSender() {
    final ServiceScheduler scheduler = new ServiceScheduler(mockContext, MetricSender.class);
    assertEquals(0, scheduler.getJobIdForService(MetricSender.class));
  }

  @Test
  public void getJobIdForService_traceSender() {
    final ServiceScheduler scheduler = new ServiceScheduler(mockContext, TraceSender.class);
    assertEquals(1, scheduler.getJobIdForService(TraceSender.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void getJobIdForService_unknown() {
    final ServiceScheduler scheduler = new ServiceScheduler(mockContext, TestSender.class);
    scheduler.getJobIdForService(TestSender.class);
  }

  /**
   * An instance of a class that extends a {@link JobService} - used only in tests in this class.
   */
  private static class TestSender extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
      return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
      return false;
    }
  }

}
