package io.bitrise.trace.data.collector.device;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DeviceNetworkTypeDataCollector}.
 */
public class DeviceNetworkTypeDataCollectorUnitTest {

    final Context mockContext = Mockito.mock(Context.class);
    final DeviceNetworkTypeDataCollector collector = new DeviceNetworkTypeDataCollector(mockContext);
    final ConnectivityManager mockConnectivityManager = Mockito.mock(ConnectivityManager.class);
    final TelephonyManager mockTelephonyManager = Mockito.mock(TelephonyManager.class);

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(0, collector.getIntervalMs());
    }

    @Test
    public void resolveNetworkType_2G() {
        assertEquals("2G", DeviceNetworkTypeDataCollector
                .resolveNetworkType(TelephonyManager.NETWORK_TYPE_GSM));
    }

    @Test
    public void resolveNetworkType_3G() {
        assertEquals("3G", DeviceNetworkTypeDataCollector
                .resolveNetworkType(TelephonyManager.NETWORK_TYPE_HSPA));
    }

    @Test
    public void resolveNetworkType_4G() {
        assertEquals("4G", DeviceNetworkTypeDataCollector
                .resolveNetworkType(TelephonyManager.NETWORK_TYPE_LTE));
    }

    @Test
    public void resolveNetworkType_5G() {
        assertEquals("5G", DeviceNetworkTypeDataCollector
                .resolveNetworkType(TelephonyManager.NETWORK_TYPE_NR));
    }

    @Test
    public void resolveNetworkType_unknown() {
        assertEquals("UNKNOWN",
                DeviceNetworkTypeDataCollector.resolveNetworkType(1234));
    }

    @Test
    public void getCellularNetworkType_noTelephonyManager() {
        assertEquals("NO_NETWORK",
                DeviceNetworkTypeDataCollector.getCellularNetworkType(null));
    }

    @Test
    public void getCellularNetworkType_3G() {
        when(mockTelephonyManager.getDataNetworkType())
                .thenReturn(TelephonyManager.NETWORK_TYPE_HSPA);
        assertEquals("3G",
                DeviceNetworkTypeDataCollector.getCellularNetworkType(mockTelephonyManager));
    }

    @Test
    public void getNetworkTypeDataFromNetworkInfo_3G() {
        final ConnectivityManager mockConnectivityManager = Mockito.mock(ConnectivityManager.class);
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(true);
        when(mockNetworkInfo.getType()).thenReturn(TelephonyManager.NETWORK_TYPE_HSPA);

        assertEquals("3G",
                DeviceNetworkTypeDataCollector.getNetworkTypeDataFromNetworkInfo(mockConnectivityManager));
    }

    @Test
    public void getNetworkTypeDataFromNetworkInfo_wifi() {
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(true);
        when(mockNetworkInfo.getType()).thenReturn(ConnectivityManager.TYPE_WIFI);

        assertEquals("WIFI",
                DeviceNetworkTypeDataCollector.getNetworkTypeDataFromNetworkInfo(mockConnectivityManager));
    }

    @Test
    public void getNetworkTypeDataFromNetworkInfo_noNetwork_noNetworkInfo() {
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(null);

        assertEquals("NO_NETWORK",
                DeviceNetworkTypeDataCollector.getNetworkTypeDataFromNetworkInfo(mockConnectivityManager));
    }

    @Test
    public void getNetworkTypeDataFromNetworkInfo_noNetwork_notConnected() {
        final NetworkInfo mockNetworkInfo = Mockito.mock(NetworkInfo.class);

        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(false);

        assertEquals("NO_NETWORK",
                DeviceNetworkTypeDataCollector.getNetworkTypeDataFromNetworkInfo(mockConnectivityManager));
    }

    @Test
    public void getDeviceNetworkType_noConnectivityManager() {
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);
        assertEquals("NO_NETWORK",
                DeviceNetworkTypeDataCollector.getDeviceNetworkType(mockContext));
    }

    @Test
    public void getNetworkTypeFromNetworkCapabilities_noNetworkCapabilities() {
        final Network mockNetwork = Mockito.mock(Network.class);

        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork)).thenReturn(null);

        assertEquals("UNKNOWN",
                DeviceNetworkTypeDataCollector.getNetworkTypeFromNetworkCapabilities(
                        mockConnectivityManager, mockContext));
    }

    @Test
    public void getNetworkTypeFromNetworkCapabilities_wifi() {
        final Network mockNetwork = Mockito.mock(Network.class);
        final NetworkCapabilities mockNetworkCapabilities = Mockito.mock(NetworkCapabilities.class);

        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
                .thenReturn(mockNetworkCapabilities);
        when(mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                .thenReturn(true);

        assertEquals("WIFI",
                DeviceNetworkTypeDataCollector.getNetworkTypeFromNetworkCapabilities(
                        mockConnectivityManager, mockContext));
    }

    @Test
    public void getNetworkTypeFromNetworkCapabilities_noWifiOrCellular() {
        final Network mockNetwork = Mockito.mock(Network.class);
        final NetworkCapabilities mockNetworkCapabilities = Mockito.mock(NetworkCapabilities.class);

        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
                .thenReturn(mockNetworkCapabilities);
        when(mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                .thenReturn(false);
        when(mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                .thenReturn(false);

        assertEquals("UNKNOWN",
                DeviceNetworkTypeDataCollector.getNetworkTypeFromNetworkCapabilities(
                        mockConnectivityManager, mockContext));
    }

    @Test
    public void getNetworkTypeFromNetworkCapabilities_cellular() {
        final Network mockNetwork = Mockito.mock(Network.class);
        final NetworkCapabilities mockNetworkCapabilities = Mockito.mock(NetworkCapabilities.class);

        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
                .thenReturn(mockNetworkCapabilities);
        when(mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                .thenReturn(false);
        when(mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                .thenReturn(true);

        assertEquals("CELLULAR",
                DeviceNetworkTypeDataCollector.getNetworkTypeFromNetworkCapabilities(
                        mockConnectivityManager, mockContext));
    }

}
