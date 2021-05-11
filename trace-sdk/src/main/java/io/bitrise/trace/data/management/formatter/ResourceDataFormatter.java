package io.bitrise.trace.data.management.formatter;

import androidx.annotation.NonNull;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.resource.ResourceLabel;

/**
 * Base abstract class for all {@link Formatter} implementations that work with {@link ResourceEntity}s.
 */
public abstract class ResourceDataFormatter extends DataFormatter {

    /**
     * Handles the Data formatting for the data that will be {@link ResourceEntity}s.
     *
     * @param data          the {@link Data} to handle.
     * @param resourceLabel the {@link ResourceLabel} for the given resource.
     * @return the {@link FormattedData[]}.
     */
    @NonNull
    public static FormattedData[] formatResource(@NonNull final Data data, @NonNull final ResourceLabel resourceLabel) {
        final Object content = data.getContent();

        if (content == null) {
            return new FormattedData[]{};
        }

        final ResourceEntity resourceEntity = new ResourceEntity(resourceLabel.getName(), content.toString());
        return new FormattedData[]{new FormattedData(resourceEntity)};
    }
}
