package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.collector.DataSource;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.management.DataFormatterDelegator;
import io.bitrise.trace.data.trace.Trace;
import io.opencensus.proto.metrics.v1.Metric;
import java.util.Objects;

/**
 * Data class for raw data objects, that will be formatted to {@link Metric}s or {@link Trace}s
 * via the {@link DataFormatterDelegator}.
 */
public class Data {

  @NonNull
  private final DataSourceType dataSourceType;

  @Nullable
  private Object content;

  /**
   * Constructor for class.
   *
   * @param dataSourceClass the Class of the {@link DataSource} itself that it produces the Data.
   */
  public Data(@NonNull final Class<? extends DataSource> dataSourceClass) {
    this(DataSourceType.valueOfName(dataSourceClass.getName()));
  }

  /**
   * Constructor for class.
   *
   * @param dataSource the {@link DataSource} itself that it produces the Data.
   */
  public Data(@NonNull final DataSource dataSource) {
    this(dataSource.getClass());
  }

  /**
   * Constructor for class.
   *
   * @param dataSourceType the {@link DataSourceType} of the Data.
   */
  public Data(@NonNull final DataSourceType dataSourceType) {
    this.dataSourceType = dataSourceType;
  }

  /**
   * Gets the {@link DataSourceType} of the Data.
   *
   * @return the DataSourceType.
   */
  @NonNull
  public DataSourceType getDataSourceType() {
    return dataSourceType;
  }

  @Nullable
  public Object getContent() {
    return content;
  }

  public void setContent(@Nullable final Object content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Data{"
        + "dataSourceType=" + dataSourceType
        + ", content=" + content
        + '}';
  }

  @Override
  public boolean equals(@Nullable final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Data)) {
      return false;
    }
    final Data data = (Data) o;
    return dataSourceType == data.dataSourceType
        && Objects.equals(content, data.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataSourceType, content);
  }
}