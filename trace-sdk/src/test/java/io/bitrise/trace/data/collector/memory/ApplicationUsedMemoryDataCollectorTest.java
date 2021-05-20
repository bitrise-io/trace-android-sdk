package io.bitrise.trace.data.collector.memory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link ApplicationUsedMemoryDataCollector}.
 */
public class ApplicationUsedMemoryDataCollectorTest {

  private final Context mockContext = Mockito.mock(Context.class);
  private final ApplicationUsedMemoryDataCollector collector =
      new ApplicationUsedMemoryDataCollector(mockContext);

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], collector.getPermissions());
  }

  @Test
  public void getIntervalMs() {
    assertEquals(15000, collector.getIntervalMs());
  }
}
