package io.bitrise.trace.data.collector.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.test.filters.SdkSuppress;

import org.junit.Test;
import org.mockito.Mockito;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Instrumented tests for {@link DeviceNetworkTypeDataCollector}.
 */
public class DeviceNetworkTypeDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    final Context mockContext = Mockito.mock(Context.class);
    final ConnectivityManager mockConnectivityManager = Mockito.mock(ConnectivityManager.class);

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void collectData_preQ() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
        assertNetworkCollectedData(collector.collectData(), "UNKNOWN");
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    public void collectData_postQ() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
        assertNetworkCollectedData(collector.collectData(), "WIFI");
    }

    private void assertNetworkCollectedData(@NonNull final Data actualData,
                                            @NonNull final String expectedContent) {
        final Data expectedData = new Data(DeviceNetworkTypeDataCollector.class);
        expectedData.setContent(expectedContent);
        assertThat(actualData, is(equalTo(expectedData)));
    }

    @Test
    public void getDeviceNetworkType_nullConnectivityManager() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(null);

        assertNetworkCollectedData(collector.collectData(), "NO_NETWORK");
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void getNetworkTypeDataFromNetworkInfo_networkInfoNull() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo())
                .thenReturn(null);

        assertNetworkCollectedData(collector.collectData(), "NO_NETWORK");
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void getNetworkTypeDataFromNetworkInfo_networkInfoWifi() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo())
                .thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected())
                .thenReturn(true);
        when(mockNetworkInfo.getType())
                .thenReturn(ConnectivityManager.TYPE_WIFI);

        assertNetworkCollectedData(collector.collectData(), "WIFI");
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void getNetworkTypeDataFromNetworkInfo_networkType_2G() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo())
                .thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected())
                .thenReturn(true);
        when(mockNetworkInfo.getType())
                .thenReturn(TelephonyManager.NETWORK_TYPE_GSM);

        assertNetworkCollectedData(collector.collectData(), "2G");
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void getNetworkTypeDataFromNetworkInfo_networkType_3G() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo())
                .thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected())
                .thenReturn(true);
        when(mockNetworkInfo.getType())
                .thenReturn(TelephonyManager.NETWORK_TYPE_HSPA);

        assertNetworkCollectedData(collector.collectData(), "3G");
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void getNetworkTypeDataFromNetworkInfo_networkType_4G() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo())
                .thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected())
                .thenReturn(true);
        when(mockNetworkInfo.getType())
                .thenReturn(TelephonyManager.NETWORK_TYPE_LTE);

        assertNetworkCollectedData(collector.collectData(), "4G");
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    public void getNetworkTypeDataFromNetworkInfo_networkType_5G() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo())
                .thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected())
                .thenReturn(true);
        when(mockNetworkInfo.getType())
                .thenReturn(TelephonyManager.NETWORK_TYPE_NR);

        assertNetworkCollectedData(collector.collectData(), "5G");
    }

    @Test
    @SdkSuppress(minSdkVersion = 23)
    public void getNetworkTypeFromNetworkCapabilities_noActiveNetwork() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetwork())
                .thenReturn(null);

        assertNetworkCollectedData(collector.collectData(), "NO_NETWORK");
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    public void getNetworkTypeFromNetworkCapabilities_noNetworkCapabilities() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
        final Network mockNetwork = Mockito.mock(Network.class);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetwork())
                .thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(any()))
                .thenReturn(null);

        assertNetworkCollectedData(collector.collectData(), "UNKNOWN");
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    public void getNetworkTypeFromNetworkCapabilities_networkCapabilitiesUnknown() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
        final Network mockNetwork = Mockito.mock(Network.class);
        final NetworkCapabilities networkCapabilities = new NetworkCapabilities();

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetwork())
                .thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(any()))
                .thenReturn(networkCapabilities);

        assertNetworkCollectedData(collector.collectData(), "UNKNOWN");
    }

    @Test
    public void getPermissions() {
        final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(context);
        assertArrayEquals(new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                collector.getPermissions());
    }
}
