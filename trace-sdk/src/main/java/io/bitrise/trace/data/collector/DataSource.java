package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;

import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.management.DataManager;

/**
 * Common interface for {@link Data} collecting sources. Each DataSource should be constructed as it should require
 * the smallest set of permissions to work. The management of DataSources happens in the {@link DataManager}, and the
 * {@link ConfigurationManager} should determine which DataSources should be used. The available DataSources should
 * be listed in the {@link DataSourceType} enum.
 */
public interface DataSource {

    /**
     * Gets the required permissions for accessing to the given DataSource.
     *
     * @return the array of permissions, empty array if none is required.
     */
    @NonNull
    String[] getPermissions();
}