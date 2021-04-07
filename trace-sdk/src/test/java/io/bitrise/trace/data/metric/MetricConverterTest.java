package io.bitrise.trace.data.metric;

import org.junit.Test;

import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.Metric;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Checks that the converting methods of {@link MetricConverter} works as intended.
 */
public class MetricConverterTest {

    private final String METRIC_APPLICATION_START_UP = "{\"metric_descriptor\":{\"name\":\"app.startup.latency.ms\",\"description\":\"App startup latency in milliseconds\",\"unit\":\"ms\",\"type\":1,\"label_keys\":[{\"key\":\"app.start.state\"}]},\"timeseries\":[{\"label_values\":[{\"value\":\"cold\"}],\"points\":[{\"value\":1234.0,\"value_case\":3,\"timestamp\":{\"seconds\":1614687783,\"nanos\":614000000}}]}]}";
    private final String PREVIOUS_METRIC_RECORD = "[10,91,10,22,97,112,112,46,115,116,97,114,116,117,112,46,108,97,116,101,110,99,121,46,109,115,18,35,65,112,112,32,115,116,97,114,116,117,112,32,108,97,116,101,110,99,121,32,105,110,32,109,105,108,108,105,115,101,99,111,110,100,115,26,2,109,115,32,1,42,22,10,15,97,112,112,46,115,116,97,114,116,46,115,116,97,116,101,18,3,107,101,121,18,33,18,6,10,4,99,111,108,100,26,23,10,12,8,-89,-36,-8,-127,6,16,-128,-53,-29,-92,2,25,0,0,0,0,0,72,-109,64]";

    /**
     * This tests the previous metric object, if the user has upgraded, they may have an older style
     * object saved, we need to ensure this does not cause any issues.
     */
    @Test
    public void toMetric_previousObjectTypes() {
        assertEquals(Metric.newBuilder().build(), MetricConverter.toMetric(PREVIOUS_METRIC_RECORD));
    }

    @Test
    public void toString_applicationStartUpMetric() {
        assertEquals(METRIC_APPLICATION_START_UP,
                MetricConverter.toString(MetricTestProvider.getApplicationStartUpMetric()));
    }

    @Test
    public void toMetric_applicationStartUpMetric() {
        assertEquals(MetricTestProvider.getApplicationStartUpMetric(),
                MetricConverter.toMetric(METRIC_APPLICATION_START_UP));
    }
}