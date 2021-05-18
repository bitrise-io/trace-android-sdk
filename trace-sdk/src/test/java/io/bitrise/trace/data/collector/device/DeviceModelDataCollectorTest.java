package io.bitrise.trace.data.collector.device;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link DeviceModelDataCollector}.
 */
public class DeviceModelDataCollectorTest {

    final DeviceModelDataCollector collector = new DeviceModelDataCollector();

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(0, collector.getIntervalMs());
    }
}
