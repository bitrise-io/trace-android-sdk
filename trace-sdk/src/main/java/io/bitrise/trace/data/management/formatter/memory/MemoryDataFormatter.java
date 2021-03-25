package io.bitrise.trace.data.management.formatter.memory;

import androidx.annotation.NonNull;

import com.google.protobuf.Timestamp;

import io.bitrise.trace.data.collector.memory.MemoryDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.management.formatter.DataFormatter;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;

import static io.bitrise.trace.data.dto.DataValues.app;
import static io.bitrise.trace.data.dto.DataValues.bytes;
import static io.bitrise.trace.data.dto.DataValues.getName;
import static io.bitrise.trace.data.dto.DataValues.memory;
import static io.bitrise.trace.data.dto.DataValues.res;
import static io.bitrise.trace.data.dto.DataValues.state;
import static io.bitrise.trace.data.dto.DataValues.system;

/**
 * Base abstract class for {@link Formatter} instances, that format memory related data.
 */
public abstract class MemoryDataFormatter extends DataFormatter {

    /**
     * Represents what kind of memory used formatter is needed.
     */
    public enum MemoryType {
        APPLICATION, SYSTEM
    }

    /**
     * Formats the common parts for {@link Metric}s, that got it's {@link Data} from a {@link MemoryDataCollector}.
     *
     * @param value the long value for the memory measurement.
     * @param memoryType the type of memory collection event e.g. system.
     * @return a {@link FormattedData} instance from the inputs.
     */
    @NonNull
    FormattedData[] handleMemoryFormatting(final long value, final @NonNull MemoryType memoryType) {
        return new FormattedData[]{new FormattedData(
                createMemoryMetric(value, memoryType, getTimestamp())
        )};
    }

    /**
     * Creates a {@link TimeSeries} for the memory value.
     *
     * @param memoryValue the value of memory at the point in time.
     * @param timestamp   the timestamp of the memory collection event.
     * @return the TimeSeries.
     */
    private static TimeSeries createTimeSeries(final long memoryValue, @NonNull final Timestamp timestamp) {
        return TimeSeries.newBuilder()
                .addLabelValues(
                        LabelValue.newBuilder()
                                .setValue(res)
                                .build())
                .addPoints(
                        Point.newBuilder()
                                .setTimestamp(timestamp)
                                .setInt64Value(memoryValue)
                                .build())
                .build();
    }

    /**
     * Creates the {@link MetricDescriptor} for the Metric.
     *
     * @param memoryType the type of memory collection event e.g. system.
     * @return the MetricDescriptor.
     */
    @NonNull
    private static MetricDescriptor createMetricDescriptor(final @NonNull MemoryType memoryType) {
        final MetricDescriptor.Builder metricDescriptor = MetricDescriptor.newBuilder();

        switch (memoryType) {
            case APPLICATION:
                metricDescriptor
                        .setDescription("App Memory Usage")
                        .setName(getName(app, memory, bytes));
                break;
            case SYSTEM:
                metricDescriptor
                        .setDescription("System Memory Usage")
                        .setName(getName(system, memory, bytes));
                break;
        }

        metricDescriptor
                .setUnit(bytes)
                .setType(MetricDescriptor.Type.GAUGE_INT64)
                .addLabelKeys(LabelKey.newBuilder()
                        .setKey(getName(memory, state))
                        .build());

        return metricDescriptor.build();
    }

    /**
     * Creates a {@link Metric} representing a memory collection event.
     *
     * @param memoryValue the value of memory at the point in time.
     * @param memoryType  the type of memory collection event e.g. system.
     * @param timestamp   the timestamp of the memory collection event.
     * @return the Metric.
     */
    @NonNull
    public static Metric createMemoryMetric(final long memoryValue, final @NonNull MemoryType memoryType,
                                            @NonNull final Timestamp timestamp) {
        return Metric.newBuilder()
                .setMetricDescriptor(createMetricDescriptor(memoryType))
                .addTimeseries(createTimeSeries(memoryValue, timestamp))
                .build();
    }
}
