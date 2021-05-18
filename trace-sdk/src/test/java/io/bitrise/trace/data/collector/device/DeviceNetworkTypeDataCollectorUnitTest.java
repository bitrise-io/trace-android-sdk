package io.bitrise.trace.data.collector.device;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link DeviceNetworkTypeDataCollector}.
 */
public class DeviceNetworkTypeDataCollectorUnitTest {

    final Context mockContext = Mockito.mock(Context.class);
    final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);


    @Test
    public void getPermissions() {
        assertArrayEquals(new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(0, collector.getIntervalMs());
    }
}
