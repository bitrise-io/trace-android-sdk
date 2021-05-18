package io.bitrise.trace.data.collector.device;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DeviceCarrierDataCollector}.
 */
public class DeviceCarrierDataCollectorTest {

    final Context mockContext = Mockito.mock(Context.class);
    final DeviceCarrierDataCollector collector = new DeviceCarrierDataCollector(mockContext);

    @Test
    public void getDeviceCarrier_telephonyManagerNull() {
        final String carrier = collector.getDeviceCarrier(null);
        assertThat(carrier, is(equalTo("NO_NETWORK")));
    }

    @Test
    public void getDeviceCarrier_networkOperatorAndSimNameEmpty() {
        final TelephonyManager mockTelephonyManager = Mockito.mock(TelephonyManager.class);

        when(mockTelephonyManager.getNetworkOperatorName()).thenReturn("");
        when(mockTelephonyManager.getSimOperatorName()).thenReturn("");

        final String carrier = collector.getDeviceCarrier(mockTelephonyManager);
        assertThat(carrier, is(equalTo("UNKNOWN_CARRIER")));
    }

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(0, collector.getIntervalMs());
    }
}
