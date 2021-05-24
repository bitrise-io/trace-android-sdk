package io.bitrise.trace.data.collector.device;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for {@link DeviceOsVersionDataCollector}.
 */
public class DeviceOsVersionDataCollectorTest {

  private final DeviceOsVersionDataCollector collector = new DeviceOsVersionDataCollector();

  @Test
  public void getPermissions() {
    assertArrayEquals(new String[0], collector.getPermissions());
  }

  @Test
  public void getIntervalMs() {
    assertEquals(0, collector.getIntervalMs());
  }
}
