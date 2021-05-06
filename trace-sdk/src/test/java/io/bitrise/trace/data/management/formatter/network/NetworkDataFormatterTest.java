package io.bitrise.trace.data.management.formatter.network;

import org.junit.Test;

import io.bitrise.trace.data.collector.device.DeviceRootedDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.dto.NetworkData;
import io.bitrise.trace.data.management.formatter.BaseDataFormatter;
import io.opencensus.proto.trace.v1.Span;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link NetworkDataFormatter}.
 */
public class NetworkDataFormatterTest extends BaseDataFormatter {
    final NetworkDataFormatter networkDataFormatter = new NetworkDataFormatter();

    @Test
    public void formatData() {
        final NetworkData networkData = new NetworkData("spanId", "parentSpanId")
                .setMethod("GET")
                .setRequestSize(100L)
                .setResponseSize(200L)
                .setUrl("https://bitrise.io")
                .setStatusCode(300)
                .setStart(400L)
                .setEnd(500L);

        final Data inputData = new Data(OkHttpDataListener.class);
        inputData.setContent(networkData);

        final FormattedData[] formattedData = networkDataFormatter.formatData(inputData);

        assertNotNull(formattedData);
        assertNotNull(formattedData[0].getSpan());

        final Span expectedSpan = NetworkDataFormatter.createNetworkSpan(networkData, networkData.getUrl());
        assertEquals(expectedSpan, formattedData[0].getSpan());
    }

    @Test
    public void formatData_notNetworkData() {
        final Data inputData = new Data(DeviceRootedDataCollector.class);
        inputData.setContent(true);

        final FormattedData[] formattedData = networkDataFormatter.formatData(inputData);

        assertNotNull(formattedData);
        assertEquals(0, formattedData.length);
    }

}