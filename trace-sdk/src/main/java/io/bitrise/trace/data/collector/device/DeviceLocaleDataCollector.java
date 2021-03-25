package io.bitrise.trace.data.collector.device;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.DataCollector;

/**
 * {@link DataCollector} type, that collects the current locale the device is using.
 * Note, this session attribute is typically captured on application start, and won't change if the user changes it
 * mid app.
 */
public class DeviceLocaleDataCollector extends DeviceDataCollector {

    @NonNull
    private final Context context;

    /**
     * Constructor for class.
     *
     * @param context the Android Context.
     */
    public DeviceLocaleDataCollector(@NonNull final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Data collectData() {
        final Data data = new Data(this);
        data.setContent(getLocale(context));
        return data;
    }

    @NonNull
    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    /**
     * Gets the current locale set on the device.
     *
     * @param context the Android Context.
     * @return the current locale of the device.
     */
    @SuppressWarnings("deprecation")
    @NonNull
    private String getLocale(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0).toString();
        } else {
            return context.getResources().getConfiguration().locale.toString();
        }
    }
}
