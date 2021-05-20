package io.bitrise.trace.data.collector.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * {@link DataCollector} type, that collects the network type of the device.
 */
public class DeviceNetworkTypeDataCollector extends DeviceDataCollector {

  /**
   * Value when the network type is unknown. Same with
   * {@link TelephonyManager#NETWORK_TYPE_UNKNOWN}.
   */
  private static final String UNKNOWN_NETWORK = "UNKNOWN";
  /**
   * Value when WIFI network is found.
   */
  private static final String WIFI = "WIFI";
  /**
   * Value when CELLULAR network is found.
   */
  private static final String CELLULAR = "CELLULAR";

  @NonNull
  private final Context context;

  /**
   * Constructor for class.
   *
   * @param context the Android Context.
   */
  public DeviceNetworkTypeDataCollector(@NonNull final Context context) {
    this.context = context;
  }

  /**
   * Gets the network type as a String value. Depending on the API level, uses different
   * methods for it.
   *
   * @param context the Android Context.
   * @return the String value network type, or {@link #UNKNOWN_NETWORK} if it cannot be determined.
   */
  @VisibleForTesting
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @NonNull
    static String getDeviceNetworkType(@NonNull final Context context) {
    final ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager == null) {
      return NO_NETWORK;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      return getNetworkTypeFromNetworkCapabilities(connectivityManager, context);
    }
    return getNetworkTypeDataFromNetworkInfo(connectivityManager);

  }

  /**
   * Gets the network type data from a {@link NetworkInfo} object.
   *
   * @param connectivityManager the ConnectivityManager from to extract the data.
   * @return the String value network type, or {@link #UNKNOWN_NETWORK} if it cannot be
   *     determined.
   * @deprecated as NetworkInfo was deprecated with API level 29, this cannot be used for newer
   *     versions of Android.
   */
  @VisibleForTesting
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @NonNull
    @Deprecated
    static String getNetworkTypeDataFromNetworkInfo(
      @NonNull final ConnectivityManager connectivityManager) {
    final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo == null || !networkInfo.isConnected()) {
      return NO_NETWORK;
    }

    final int networkType = networkInfo.getType();
    if (networkType == ConnectivityManager.TYPE_WIFI) {
      return WIFI;
    }
    return resolveNetworkType(networkType);
  }

  /**
   * Gets the network type data from a {@link NetworkCapabilities} object.
   *
   * @param connectivityManager the ConnectivityManager from to extract the data.
   * @param context             the Android Context.
   * @return the String value network type, or {@link #UNKNOWN_NETWORK} if it cannot be
   *     determined. If the app has {@link Manifest.permission#READ_PHONE_STATE} it will return the
   *     exact network type, otherwise it can determine if it uses wifi or cellular connection.
   */
  @VisibleForTesting
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    static String getNetworkTypeFromNetworkCapabilities(
      @NonNull final ConnectivityManager connectivityManager,
      @NonNull final Context context) {
    final Network activeNetwork = connectivityManager.getActiveNetwork();
    if (activeNetwork == null) {
      return NO_NETWORK;
    }
    final NetworkCapabilities networkCapabilities =
        connectivityManager.getNetworkCapabilities(activeNetwork);
    if (networkCapabilities == null) {
      return UNKNOWN_NETWORK;
    }
    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
      return WIFI;
    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N && context.checkSelfPermission(
          Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                return getCellularNetworkType(telephonyManager);
      } else {
        return CELLULAR;
      }
    }
    return UNKNOWN_NETWORK;
  }

  /**
   * Gets the type of the cellular network with the help of the TelephonyManager.
   *
   * @param telephonyManager the telephonyManager from the context.
   * @return the String value network type, or {@link #UNKNOWN_NETWORK} if it cannot be
   *     determined. If the app has {@link Manifest.permission#READ_PHONE_STATE} it will return the
   *     exact network type, otherwise it can determine if it uses wifi or cellular connection.
   */
  @VisibleForTesting
    @RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    @NonNull
    static String getCellularNetworkType(@Nullable final TelephonyManager telephonyManager) {
    if (telephonyManager == null) {
      return NO_NETWORK;
    }

    return resolveNetworkType(telephonyManager.getDataNetworkType());
  }

  /**
   * Resolves the network type int value to a readable String value of different mobile network
   * generations.
   *
   * @param networkType the {@link TelephonyManager} network type enum value.
   * @return the String value network type, or {@link #UNKNOWN_NETWORK} if it cannot be determined.
   */
  @NonNull
  @VisibleForTesting
    static String resolveNetworkType(final int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case 16: //TelephonyManager.NETWORK_TYPE_GSM
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17: //TelephonyManager.NETWORK_TYPE_TD_SCDMA
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18: // TelephonyManager.NETWORK_TYPE_IWLAN
            case 19: // LTE_CA
                return "4G";
            case 20: // TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return UNKNOWN_NETWORK;
        }
    }

  @NonNull
  @Override
  public Data collectData() {
    final Data data = new Data(this);
    data.setContent(getDeviceNetworkType(context));
    return data;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[] {android.Manifest.permission.ACCESS_NETWORK_STATE};
  }
}
