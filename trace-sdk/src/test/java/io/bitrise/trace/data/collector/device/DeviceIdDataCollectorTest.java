package io.bitrise.trace.data.collector.device;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link DeviceIdDataCollector}.
 */
public class DeviceIdDataCollectorTest {

  final Context mockContext = Mockito.mock(Context.class);
  final DeviceIdDataCollector deviceIdDataCollector = new DeviceIdDataCollector(mockContext);

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], deviceIdDataCollector.getPermissions());
  }

  @Test
  public void getIntervalMs() {
    assertEquals(0, deviceIdDataCollector.getIntervalMs());
  }
}
