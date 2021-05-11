package io.bitrise.trace.data.management.formatter.application;

import androidx.annotation.NonNull;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.management.formatter.ResourceDataFormatter;
import io.bitrise.trace.data.resource.ResourceLabel;

/**
 * {@link Formatter} implementation, to handle formatting for {@link DataSourceType#APP_VERSION_NAME}.
 */
public class ApplicationVersionNameDataFormatter extends ResourceDataFormatter {

    @NonNull
    @Override
    public FormattedData[] formatData(@NonNull final Data data) {
        return formatResource(data, ResourceLabel.APPLICATION_VERSION_NAME);
    }
}
