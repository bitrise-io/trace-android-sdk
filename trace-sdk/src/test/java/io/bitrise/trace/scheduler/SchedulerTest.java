package io.bitrise.trace.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Scheduler}.
 */
public class SchedulerTest {

  final Context mockContext = Mockito.mock(Context.class);
  final Runnable mockRunnable = Mockito.mock(Runnable.class);
  final ExecutorScheduler scheduler = new ExecutorScheduler(mockContext, mockRunnable);

  @Test
  public void schedule() {
    final Integer id = scheduler.schedule();
    assertEquals(new Integer(-1), id);
    assertNotNull(scheduler.scheduledFuture);
    assertTrue(scheduler.scheduledFuture.getDelay(TimeUnit.MILLISECONDS) <= 0);
    // delay might have already elapsed, which makes the delay negative
  }

  @Test
  public void scheduleDelayed() {
    final Integer id = scheduler.scheduleDelayed();
    assertEquals(new Integer(-1), id);
    assertNotNull(scheduler.scheduledFuture);
    assertTrue(scheduler.scheduledFuture.getDelay(TimeUnit.MILLISECONDS)
        > (Scheduler.DEFAULT_SCHEDULE_INITIAL_DELAY_MS - 10));
    // we cannot get the delay immediately, several milliseconds may have passed.
  }

  @Test
  public void calculateDelay_inFuture() {
    final long delay = scheduler.calculateDelay(System.currentTimeMillis() + 100);
    assertTrue(delay > 0);
  }

  @Test
  public void calculateDelay_timePassed() {
    final long delay = scheduler.calculateDelay(System.currentTimeMillis() - 100);
    assertEquals(0, delay);
  }

}
