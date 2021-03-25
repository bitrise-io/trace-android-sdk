package io.bitrise.trace.data.management.formatter;

import androidx.annotation.NonNull;

import com.google.protobuf.Timestamp;

import java.util.ArrayList;
import java.util.List;

import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;
import io.opencensus.proto.trace.v1.Span;

/**
 * Base abstract class for all {@link Formatter} implementations.
 */
public abstract class DataFormatter implements Formatter {

    /**
     * Creates a {@link Timestamp} with the current system time.
     *
     * @return the Timestamp.
     */
    @NonNull
    protected Timestamp getTimestamp() {
        return TraceClock.getCurrentTimestamp();
    }

    /**
     * Formats a List of Spans to an Array of {@link FormattedData}.
     *
     * @param spans the List of the given Spans.
     * @return the Array of FormattedData.
     */
    @NonNull
    public static FormattedData[] toFormattedDataArray(@NonNull final List<Span> spans) {
        return toFormattedDataArray(spans.toArray(new Span[]{}));
    }

    /**
     * Formats {@link Span}s to an Array of {@link FormattedData}.
     *
     * @param spans the given Spans.
     * @return the Array of FormattedData.
     */
    @NonNull
    public static FormattedData[] toFormattedDataArray(@NonNull final Span... spans) {
        final List<FormattedData> formattedDataList = new ArrayList<>();
        for (@NonNull final Span span : spans) {
            formattedDataList.add(new FormattedData(span));
        }
        return formattedDataList.toArray(new FormattedData[]{});
    }
}
