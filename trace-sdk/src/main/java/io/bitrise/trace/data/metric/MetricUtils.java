package io.bitrise.trace.data.metric;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.opencensus.proto.metrics.v1.LabelKey;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.metrics.v1.MetricDescriptor;

/**
 * Utility methods related to {@link Metric}.
 */
public class MetricUtils {

    private MetricUtils() {
        throw new IllegalStateException("Should not be instantiated, used only for storing static members!");
    }

    /**
     * Converts a List of {@link MetricEntity}s to a List of {@link Metric}.
     *
     * @param metricEntityList the List to convert.
     * @return the converted List.
     */
    public static List<Metric> toMetricList(@NonNull final List<MetricEntity> metricEntityList) {
        final List<Metric> metricList = new ArrayList<>();
        for (@NonNull final MetricEntity metricEntity : metricEntityList) {
            metricList.add(metricEntity.getMetric());
        }
        return metricList;
    }

    /**
     * Batches multiple {@link Metric}s to a single one. Metrics should be batched, when they have the same name.
     *
     * @param metrics the Metrics to batch.
     * @return the batched Metrics.
     */
    @NonNull
    public static List<Metric> batchMetrics(@NonNull final Metric... metrics) {
        final Map<String, ArrayList<Metric>> groupedMetrics = groupMetrics(metrics);
        final List<Metric> batchedMetrics = new ArrayList<>();
        for (@NonNull final Map.Entry<String, ArrayList<Metric>> mapEntry : groupedMetrics.entrySet()) {
            final Metric batchedMetric = batchMetricsWithName(mapEntry.getKey(), mapEntry.getValue());
            if (!isEmpty(batchedMetric)) {
                batchedMetrics.add(batchedMetric);
            }
        }
        return batchedMetrics;
    }

    /**
     * Batches multiple {@link Metric}s to a single one. Metrics should be batched, when they have the same name.
     * Metrics with different names will be ignored.
     *
     * @param metrics the Metrics to batch.
     * @param name    the Metric name to batch.
     * @return the batched Metrics.
     */
    @NonNull
    public static Metric batchMetricsWithName(@NonNull final String name, @NonNull final List<Metric> metrics) {
        final Metric.Builder metricBuilder = Metric.newBuilder();
        for (@NonNull final Metric metric : metrics) {
            if (name.equals(metric.getMetricDescriptor().getName())) {
                metricBuilder.mergeFrom(metric);
            }
        }

        return flattenAttributes(metricBuilder);
    }

    /**
     * Flattens the attributes of the merged {@link Metric}s. If a {@link LabelKey} is present multiple times, it
     * removes the duplicates.
     *
     * @param metricBuilder the {@link Metric.Builder} to work with.
     * @return the flattened Metric.
     */
    @NonNull
    public static Metric flattenAttributes(@NonNull final Metric.Builder metricBuilder) {
        final Set<LabelKey> labelKeySet = new HashSet<>(metricBuilder.getMetricDescriptor().getLabelKeysList());
        final MetricDescriptor.Builder metricDescriptorBuilder = metricBuilder.getMetricDescriptor().toBuilder();
        metricDescriptorBuilder.clearLabelKeys().addAllLabelKeys(labelKeySet);
        metricBuilder.setMetricDescriptor(metricDescriptorBuilder.build());
        return metricBuilder.build();
    }

    /**
     * Checks if the given {@link Metric} is empty or not. The Metric is considered empty, when the name of it is empty.
     *
     * @param metric the given Metric.
     * @return {@code true} when it is empty, {@code false} otherwise.
     */
    public static boolean isEmpty(@NonNull final Metric metric) {
        return metric.getMetricDescriptor().getName().equals("");
    }

    /**
     * Groups {@link Metric}s based on their name.
     *
     * @param metrics the Metrics to group.
     * @return a Map of the grouped Metrics.
     */
    @NonNull
    public static Map<String, ArrayList<Metric>> groupMetrics(@NonNull final Metric... metrics) {
        final Map<String, ArrayList<Metric>> metricMap = new HashMap<>();
        for (@NonNull final Metric metric : metrics) {
            final String metricName = metric.getMetricDescriptor().getName();
            if (metricMap.containsKey(metricName)) {
                final ArrayList<Metric> entryValue = metricMap.get(metricName);
                entryValue.add(metric);
                metricMap.put(metricName, entryValue);
            } else {
                metricMap.put(metricName, new ArrayList<Metric>() {{
                    add(metric);
                }});
            }
        }
        return metricMap;
    }
}
