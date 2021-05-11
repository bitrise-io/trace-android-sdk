package io.bitrise.trace.data.management.formatter.memory;

import org.junit.Test;

import io.bitrise.trace.data.collector.memory.SystemMemoryDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.MetricDescriptor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link SystemMemoryDataFormatter}.
 */
public class SystemMemoryDataFormatterTest extends BaseDataFormatterTest {

    final SystemMemoryDataFormatter formatter = new SystemMemoryDataFormatter();

    @Test
    public void formatData_contentNull() {
        final Data data = new Data(SystemMemoryDataCollector.class);
        data.setContent(null);
        assertArrayEquals(new FormattedData[]{}, formatter.formatData(data));
    }

    @Test
    public void handleMemoryFormatting() {
        final Long memoryUsed = 2345L;

        final Data data = new Data(SystemMemoryDataCollector.class);
        data.setContent(memoryUsed);
        final FormattedData[] formattedData = formatter.formatData(data);
        assertEquals(1, formattedData.length);

        final MetricDescriptor expectedMetricDescriptor = MetricDescriptor.newBuilder()
                .setDescription("System Memory Usage")
                .setName("system.memory.bytes")
                .setUnit("bytes")
                .setType(MetricDescriptor.Type.GAUGE_INT64)
                .addLabelKeys(LabelKey.newBuilder()
                        .setKey("memory.state"))
                .build();

        assertEquals(expectedMetricDescriptor, formattedData[0].getMetricEntity().getMetric().getMetricDescriptor());
    }
}
