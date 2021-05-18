package io.bitrise.trace.data.collector.device;

import android.content.Context;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.DataCollector;

/**
 * {@link DataCollector} type, that collects the carrier of the device.
 */
public class DeviceCarrierDataCollector extends DeviceDataCollector {

    /**
     * Value when the carrier cannot be determined.
     */
    private static final String UNKNOWN_CARRIER = "UNKNOWN_CARRIER";

    @NonNull
    private final Context context;

    /**
     * Constructor for class.
     *
     * @param context the Android Context.
     */
    public DeviceCarrierDataCollector(@NonNull final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Data collectData() {
        final Data data = new Data(this);
        data.setContent(getDeviceCarrier((TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE)));
        return data;
    }

    @NonNull
    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    /**
     * Gets the carrier of the currently active network.
     *
     * @param telephonyManager the telephonyManager to use from context.
     * @return the carrier of the currently active network.
     */
    @VisibleForTesting
    @NonNull
    String getDeviceCarrier(@Nullable final TelephonyManager telephonyManager) {
        if (telephonyManager == null) {
            return NO_NETWORK;
        }
        String operatorName = telephonyManager.getNetworkOperatorName();
        if (operatorName.isEmpty()) {
            operatorName = telephonyManager.getSimOperatorName();
            if (operatorName.isEmpty()) {
                operatorName = UNKNOWN_CARRIER;
            }
        }
        return operatorName;
    }
}
