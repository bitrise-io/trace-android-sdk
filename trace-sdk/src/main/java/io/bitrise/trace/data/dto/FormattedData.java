package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.trace.v1.Span;

/**
 * Data class for {@link Span}, {@link MetricEntity} and {@link ResourceEntity} classes.
 */
public class FormattedData {

  @Nullable
  private MetricEntity metricEntity;

  @Nullable
  private Span span;

  @Nullable
  private ResourceEntity resourceEntity;

  /**
   * Constructor for class. Use this when it should transfer a {@link Span}.
   *
   * @param span the Span.
   */
  public FormattedData(@NonNull final Span span) {
    this.span = span;
  }

  /**
   * Constructor for class. Use this when it should transfer a {@link Metric}.
   *
   * @param metric the Metric.
   */
  public FormattedData(@NonNull final Metric metric) {
    this.metricEntity = new MetricEntity(metric);
  }

  /**
   * Constructor for class. Use this when it should transfer a {@link ResourceEntity}.
   *
   * @param resourceEntity the ResourceEntity.
   */
  public FormattedData(@NonNull final ResourceEntity resourceEntity) {
    this.resourceEntity = resourceEntity;
  }

  @Nullable
  public MetricEntity getMetricEntity() {
    return metricEntity;
  }

  @Nullable
  public Span getSpan() {
    return span;
  }

  @Nullable
  public ResourceEntity getResourceEntity() {
    return resourceEntity;
  }

  @Override
  public String toString() {
    return "FormattedData{"
        + "metricEntity=" + metricEntity
        + ", span=" + span
        + ", resourceEntity=" + resourceEntity
        + '}';
  }
}
