package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;

import io.bitrise.trace.data.collector.application.ApplicationVersionCodeDataCollector;
import io.bitrise.trace.data.collector.application.ApplicationVersionNameDataCollector;
import io.bitrise.trace.data.collector.cpu.ApplicationCpuUsageDataCollector;
import io.bitrise.trace.data.collector.cpu.SystemCpuUsageDataCollector;
import io.bitrise.trace.data.collector.device.DeviceCarrierDataCollector;
import io.bitrise.trace.data.collector.device.DeviceIdDataCollector;
import io.bitrise.trace.data.collector.device.DeviceLocaleDataCollector;
import io.bitrise.trace.data.collector.device.DeviceModelDataCollector;
import io.bitrise.trace.data.collector.device.DeviceNetworkTypeDataCollector;
import io.bitrise.trace.data.collector.device.DeviceOsVersionDataCollector;
import io.bitrise.trace.data.collector.device.DeviceRootedDataCollector;
import io.bitrise.trace.data.collector.memory.ApplicationUsedMemoryDataCollector;
import io.bitrise.trace.data.collector.memory.SystemMemoryDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.collector.view.ActivityStateDataListener;
import io.bitrise.trace.data.collector.view.ApplicationStartUpDataListener;
import io.bitrise.trace.data.collector.view.FragmentStateDataListener;

/**
 * The types for {@link DataSource}s.
 */
public enum DataSourceType {
    ACTIVITY_STATE(ActivityStateDataListener.class.getName()),
    APP_CPU_USAGE(ApplicationCpuUsageDataCollector.class.getName()),
    APP_START(ApplicationStartUpDataListener.class.getName()),
    APP_USED_MEMORY(ApplicationUsedMemoryDataCollector.class.getName()),
    APP_VERSION_CODE(ApplicationVersionCodeDataCollector.class.getName()),
    APP_VERSION_NAME(ApplicationVersionNameDataCollector.class.getName()),
    DEVICE_CARRIER(DeviceCarrierDataCollector.class.getName()),
    DEVICE_LOCALE(DeviceLocaleDataCollector.class.getName()),
    DEVICE_ID(DeviceIdDataCollector.class.getName()),
    DEVICE_MODEL(DeviceModelDataCollector.class.getName()),
    DEVICE_OS(DeviceOsVersionDataCollector.class.getName()),
    DEVICE_ROOTED(DeviceRootedDataCollector.class.getName()),
    FRAGMENT_STATE(FragmentStateDataListener.class.getName()),
    NETWORK_CALL_OKHTTP(OkHttpDataListener.class.getName()),
    NETWORK_TYPE(DeviceNetworkTypeDataCollector.class.getName()),
    SYSTEM_CPU_USAGE(SystemCpuUsageDataCollector.class.getName()),
    SYSTEM_USED_MEMORY(SystemMemoryDataCollector.class.getName());

    /**
     * The name of the class that is linked to this DataSourceType.
     */
    public final String className;

    /**
     * Constructor for class.
     *
     * @param className the name of the class.
     */
    DataSourceType(@NonNull final String className) {
        this.className = className;
    }

    /**
     * Gets the DataSourceType for the given class name.
     *
     * @param className the name of the class.
     * @return the DataSourceType.
     */
    public static DataSourceType valueOfName(@NonNull final String className) {
        for (@NonNull final DataSourceType dataSourceType : values()) {
            if (dataSourceType.className.equalsIgnoreCase(className)) {
                return dataSourceType;
            }
        }
        throw new IllegalArgumentException("No such data source");
    }
}

