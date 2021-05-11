package io.bitrise.trace.data.collector.device;

import android.telephony.TelephonyManager;

import org.junit.Test;
import org.mockito.Mockito;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

/**
 * Instrumented tests for {@link DeviceCarrierDataCollector}.
 */
public class DeviceCarrierDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    final DeviceCarrierDataCollector collector = new DeviceCarrierDataCollector(context);

    /**
     * Verifies that when {@link DeviceCarrierDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final Data expectedData = new Data(DeviceCarrierDataCollector.class);
        expectedData.setContent("Android");
        assertThat(collector.collectData(), is(equalTo(expectedData)));
    }

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
}
