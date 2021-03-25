package io.bitrise.trace.data.metric;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

/**
 * Contains tests for {@link MetricUtils} class.
 */
public class MetricUtilsTest {

    /**
     * Tests {@link MetricUtils#batchMetrics(Metric...)}, that the batched Metric should be a merge of these Metrics.
     */
    @Test
    public void batchMetrics_shouldEqual() {
        final Metric metric1 = MetricTestProvider.getSampleMetric();
        final Metric metric2 = MetricTestProvider.getSampleMetricWithOtherTimeSeries();
        final Metric actualValue = MetricUtils.batchMetrics(metric1, metric2).get(0);
        final Metric expectedValue = MetricTestProvider.getMergedSampleMetric();
        assertThat(actualValue, equalTo(expectedValue));
    }

    /**
     * Tests {@link MetricUtils#batchMetricsWithName(String, List)}, that Metrics with different name than the given
     * value, should not be merged to the batched value.
     */
    @Test
    public void batchMetricsWithName_shouldNotContainDifferentName() {
        final Metric metric1 = MetricTestProvider.getSampleMetric();
        final Metric metric2 = MetricTestProvider.getSampleMetricWithOtherTimeSeries();
        final Metric metric3 = MetricTestProvider.getOtherMetric();
        final Metric actualValue = MetricUtils.batchMetricsWithName(metric1.getMetricDescriptor().getName(),
                Arrays.asList(metric1, metric2, metric3));
        final Metric expectedValue = MetricTestProvider.getMergedSampleMetric();
        assertThat(actualValue, equalTo(expectedValue));
    }

    /**
     * Tests {@link MetricUtils#isEmpty(Metric)} that should return {@code true} when a Metric with empty name is used.
     */
    @Test
    public void isEmpty_shouldBeEmpty() {
        final Metric actualValue = MetricTestProvider.getEmptyMetric();
        assertThat(MetricUtils.isEmpty(actualValue), is(true));
    }

    /**
     * Tests {@link MetricUtils#isEmpty(Metric)} that should return {@code false} when a Metric with non empty name is
     * used.
     */
    @Test
    public void isEmpty_shouldBeNotEmpty() {
        final Metric actualValue = MetricTestProvider.getSampleMetric();
        assertThat(MetricUtils.isEmpty(actualValue), is(false));
    }

    /**
     * Tests {@link MetricUtils#groupMetrics(Metric...)} should create a map with the key of the Metric names and the
     * value of the belonging Metrics. The result should contain two groups, 'Sample metric' and 'Different metric',
     * the first with values {@link MetricTestProvider#getSampleMetric()} and
     * {@link MetricTestProvider#getSampleMetricWithOtherTimeSeries()}, the second with
     * {@link MetricTestProvider#getOtherMetric()}.
     */
    @Test
    public void groupMetrics_shouldBeGroupedByName() {
        final Metric metric1 = MetricTestProvider.getSampleMetric();
        final Metric metric2 = MetricTestProvider.getSampleMetricWithOtherTimeSeries();
        final Metric metric3 = MetricTestProvider.getOtherMetric();
        final Map<String, ArrayList<Metric>> actualValue = MetricUtils.groupMetrics(metric1, metric2, metric3);

        final String expectedGroupName1 = metric1.getMetricDescriptor().getName();
        final String expectedGroupName2 = metric3.getMetricDescriptor().getName();
        assertThat(actualValue, hasKey(expectedGroupName1));
        assertThat(actualValue, hasKey(expectedGroupName2));
        assertThat(actualValue.get(expectedGroupName1), containsInAnyOrder(metric1, metric2));
        assertThat(actualValue.get(expectedGroupName2), containsInAnyOrder(metric3));
    }

    /**
     * When multiple {@link MetricDescriptor}s have {@link LabelKey}s that are the same,
     * {@link MetricUtils#flattenAttributes(Metric.Builder)} should remove the duplicates.
     */
    @Test
    public void flattenAttributes_shouldRemoveDuplicates() {
        final Metric metric1 = MetricTestProvider.getSampleMetric();
        final Metric metric2 = MetricTestProvider.getSampleMetricWithOtherTimeSeries();

        final Metric mergedMetric = metric1.toBuilder().mergeFrom(metric2).build();
        assertThat(mergedMetric.getMetricDescriptor().getLabelKeysCount(), is(2));

        final Metric flattenedMetric = MetricUtils.flattenAttributes(mergedMetric.toBuilder());
        assertThat(flattenedMetric.getMetricDescriptor().getLabelKeysCount(), is(1));
    }
}