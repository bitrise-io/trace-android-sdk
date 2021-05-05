package io.bitrise.trace.data.management.formatter.memory;

import org.junit.Test;

import io.bitrise.trace.data.collector.memory.ApplicationUsedMemoryDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatterTest;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.MetricDescriptor;

import static io.bitrise.trace.data.dto.DataValues.bytes;
import static io.bitrise.trace.data.dto.DataValues.getName;
import static io.bitrise.trace.data.dto.DataValues.memory;
import static io.bitrise.trace.data.dto.DataValues.state;
import static io.bitrise.trace.data.dto.DataValues.system;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ApplicationUsedMemoryDataFormatter}.
 */
public class ApplicationUsedMemoryDataFormatterTest extends BaseDataFormatterTest {

    final ApplicationUsedMemoryDataFormatter formatter = new ApplicationUsedMemoryDataFormatter();
    final Long memoryUsed = 1234L;

    @Test
    public void formatData_contentNull() {
        final Data data = new Data(ApplicationUsedMemoryDataCollector.class);
        data.setContent(null);
        assertArrayEquals(new FormattedData[]{}, formatter.formatData(data));
    }

    @Test
    public void handleMemoryFormatting() {
        final Data data = new Data(ApplicationUsedMemoryDataCollector.class);
        data.setContent(memoryUsed);
        final FormattedData[] formattedData = formatter.formatData(data);
        assertEquals(1, formattedData.length);

        final MetricDescriptor expectedMetricDescriptor = MetricDescriptor.newBuilder()
                .setDescription("App Memory Usage")
                .setName("app.memory.bytes")
                .setUnit("bytes")
                .setType(MetricDescriptor.Type.GAUGE_INT64)
                .addLabelKeys(LabelKey.newBuilder()
                        .setKey("memory.state"))
                .build();

        assertEquals(expectedMetricDescriptor, formattedData[0].getMetricEntity().getMetric().getMetricDescriptor());
    }
}
