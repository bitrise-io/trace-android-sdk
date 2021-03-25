package io.bitrise.trace.data.collector.device;

import android.os.Build;

import androidx.annotation.NonNull;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.DataCollector;

/**
 * {@link DataCollector} type, that collects the model of the device.
 */
public class DeviceModelDataCollector extends DeviceDataCollector {

    @NonNull
    @Override
    public Data collectData() {
        final Data data = new Data(this);
        data.setContent(getDeviceModel());
        return data;
    }

    @NonNull
    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    /**
     * Gets the model of the current device.
     *
     * @return the model of the current device.
     */
    @NonNull
    private String getDeviceModel() {
        return Build.MODEL;
    }
}
