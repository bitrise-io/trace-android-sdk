package io.bitrise.trace.data.collector.device;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link DeviceLocaleDataCollector}.
 */
public class DeviceLocaleDataCollectorTest {

    final Context mockContext = Mockito.mock(Context.class);
    final DeviceLocaleDataCollector collector = new DeviceLocaleDataCollector(mockContext);

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(0, collector.getIntervalMs());
    }
}
