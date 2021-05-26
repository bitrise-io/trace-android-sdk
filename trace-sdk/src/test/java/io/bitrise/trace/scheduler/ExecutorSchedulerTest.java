package io.bitrise.trace.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.annotation.Nullable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ExecutorScheduler}.
 */
public class ExecutorSchedulerTest {

  private final Context mockContext = Mockito.mock(Context.class);
  private final Runnable mockRunnable = Mockito.mock(Runnable.class);
  private final long initialDelay = 600;

  @Test
  public void scheduleDelayed_noRunnable() {
    final ExecutorScheduler executorScheduler = new ExecutorScheduler(mockContext, null);

    assertNull(executorScheduler.runnable);
    assertNull(executorScheduler.scheduleDelayed(initialDelay));
  }

  @Test
  public void scheduleDelayed_noExecutiveService() {
    final ExecutorScheduler executorScheduler = new ExecutorScheduler(mockContext, mockRunnable);
    assertNull(executorScheduler.scheduledExecutorService);

    executorScheduler.scheduleDelayed(initialDelay);

    final ScheduledThreadPoolExecutor executor = getScheduledThreadPoolExecutorFromService(
        executorScheduler.scheduledExecutorService);
    assertEquals(1, executor.getPoolSize());
  }

  @Test
  public void scheduleDelayed_intervalGreaterThanZero() {
    final ExecutorScheduler executorScheduler =
        new ExecutorScheduler(mockContext, mockRunnable, initialDelay, 200);

    executorScheduler.scheduleDelayed(initialDelay);

    assertNotNull(executorScheduler.scheduledFuture);
    assertTrue(executorScheduler.scheduledFuture.getDelay(TimeUnit.MILLISECONDS)
        > (initialDelay - 100));
    // we cannot get the delay immediately, several milliseconds will have passed.

    final ScheduledThreadPoolExecutor executor = getScheduledThreadPoolExecutorFromService(
        executorScheduler.scheduledExecutorService);
    assertEquals(1, executor.getQueue().size());
  }

  @Test
  public void scheduleDelayed_intervalZero() {
    final ExecutorScheduler executorScheduler =
        new ExecutorScheduler(mockContext, mockRunnable, initialDelay, 0);

    executorScheduler.scheduleDelayed(initialDelay);

    assertNotNull(executorScheduler.scheduledFuture);
    assertTrue(executorScheduler.scheduledFuture.getDelay(TimeUnit.MILLISECONDS)
        > (initialDelay - 100));
    // we cannot get the delay immediately, several milliseconds will have passed.

    final ScheduledThreadPoolExecutor executor = getScheduledThreadPoolExecutorFromService(
        executorScheduler.scheduledExecutorService);
    assertEquals(1, executor.getQueue().size());
  }

  /**
   * When there are scheduled tasks, assert when they are cancelled the scheduler is shut down
   * and there are zero tasks in the queue.
   */
  @Test
  public void cancelAll() {
    final ExecutorScheduler executorScheduler =
        new ExecutorScheduler(mockContext, mockRunnable);

    assertEquals(new Integer(-1), executorScheduler.scheduleDelayed(initialDelay));
    assertNotNull(executorScheduler.scheduledExecutorService);
    assertFalse(executorScheduler.scheduledExecutorService.isShutdown());

    executorScheduler.cancelAll();
    assertTrue(executorScheduler.scheduledExecutorService.isShutdown());

    final ScheduledThreadPoolExecutor executor = getScheduledThreadPoolExecutorFromService(
        executorScheduler.scheduledExecutorService);
    assertEquals(0, executor.getQueue().size());
  }

  /**
   * Creates a {@link ScheduledThreadPoolExecutor} from a given {@link ScheduledExecutorService}.
   *
   * @Return a {@link ScheduledThreadPoolExecutor} from a given {@link ScheduledExecutorService}.
   */
  private ScheduledThreadPoolExecutor getScheduledThreadPoolExecutorFromService(
      @Nullable final ScheduledExecutorService scheduledExecutorService) {
    assert scheduledExecutorService instanceof ScheduledThreadPoolExecutor;
    return (ScheduledThreadPoolExecutor) scheduledExecutorService;
  }

}
