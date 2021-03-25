package io.bitrise.trace.data.management.formatter.memory;

import com.google.protobuf.Timestamp;

import org.junit.Test;
import org.mockito.Mockito;

import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.bitrise.trace.data.management.formatter.memory.MemoryDataFormatter;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.LabelValue;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.metrics.v1.TimeSeries;

import static io.bitrise.trace.data.dto.DataValues.bytes;
import static io.bitrise.trace.data.dto.DataValues.getName;
import static io.bitrise.trace.data.dto.DataValues.memory;
import static io.bitrise.trace.data.dto.DataValues.res;
import static io.bitrise.trace.data.dto.DataValues.state;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link MemoryDataFormatter}.
 */
public class MemoryDataFormatterTest extends BaseDataFormatterTest {

    final MemoryDataFormatter mockMemoryDataFormatter = Mockito.mock(
            MemoryDataFormatter.class,
            Mockito.CALLS_REAL_METHODS);

    @Test
    public void handleMemoryFormatting() {
        final long inputValue = 21;

        final FormattedData[] output = mockMemoryDataFormatter
                .handleMemoryFormatting(inputValue, MemoryDataFormatter.MemoryType.SYSTEM);

        assertNotNull(output);
        assertNotNull(output[0].getMetricEntity());

        final Metric outputMetric = output[0].getMetricEntity().getMetric();

        final MetricDescriptor expectedMetricDescriptor = MetricDescriptor.newBuilder()
                        .setName("system.memory.bytes")
                        .setDescription("System Memory Usage")
                        .setUnit(bytes)
                        .setType(MetricDescriptor.Type.GAUGE_INT64)
                        .addLabelKeys(LabelKey.newBuilder()
                            .setKey(getName(memory, state))
                            .build())
                        .build();

        final Timestamp expectedTimestamp = outputMetric.getTimeseries(0).getPoints(0).getTimestamp();

        final TimeSeries expectedTimeSeries =
                TimeSeries.newBuilder()
                        .addLabelValues(LabelValue.newBuilder()
                                        .setValue(res)
                                        .build())
                        .addPoints(Point.newBuilder()
                                        .setTimestamp(expectedTimestamp)
                                        .setInt64Value(21L)
                                        .build())
                        .build();

        final Metric expectedMemoryMetric =
                Metric.newBuilder()
                        .setMetricDescriptor(expectedMetricDescriptor)
                        .addTimeseries(expectedTimeSeries)
                        .build();

        assertEquals(expectedMemoryMetric, outputMetric);

    }
}
