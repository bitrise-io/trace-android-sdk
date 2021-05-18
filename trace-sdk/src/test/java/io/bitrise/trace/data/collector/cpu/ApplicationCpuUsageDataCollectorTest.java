package io.bitrise.trace.data.collector.cpu;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ApplicationCpuUsageDataCollector}.
 */
public class ApplicationCpuUsageDataCollectorTest {

    private final ApplicationCpuUsageDataCollector collector = new ApplicationCpuUsageDataCollector();

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(15000, collector.getIntervalMs());
    }
}
