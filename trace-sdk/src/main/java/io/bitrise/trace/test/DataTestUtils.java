package io.bitrise.trace.test;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.protobuf.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.resource.ResourceLabel;
import io.bitrise.trace.network.MetricRequest;
import io.bitrise.trace.session.Session;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.resource.v1.Resource;

// TODO move this to a new library, or remove with proguard.

/**
 * Utility class for {@link Data} related test classes.
 */
public class DataTestUtils {

    private DataTestUtils() {
        throw new IllegalStateException("Should not be instantiated, used only for storing static members!");
    }

    /**
     * Creates a {@link MetricRequest} with the given List of {@link Metric}s.
     *
     * @param metrics the Metrics to add to the TraceRequest.
     * @return the created TraceRequest.
     */
    @NonNull
    public static MetricRequest getMetricRequest(@NonNull final List<Metric> metrics) {
        return new MetricRequest(getSampleResource(null), metrics);
    }

    /**
     * Creates a sample {@link Resource}.
     *
     * @param sessionId the {@link Session} ID to use.
     * @return the created Resource.
     */
    @NonNull
    public static Resource getSampleResource(@Nullable final String sessionId) {
        final Resource.Builder resource = Resource.newBuilder();
        resource.setType("mobile");
        resource.putLabels(ResourceLabel.APPLICATION_VERSION_CODE.getName(), "21");
        resource.putLabels(ResourceLabel.APPLICATION_VERSION_NAME.getName(), "2.1.0");
        resource.putLabels(ResourceLabel.APPLICATION_PLATFORM.getName(), "android");
        resource.putLabels(ResourceLabel.DEVICE_CARRIER.getName(), "Telenor Hu");
        resource.putLabels(ResourceLabel.DEVICE_LOCALE.getName(), "en_US");
        resource.putLabels(ResourceLabel.DEVICE_ID.getName(), "01DJ5NXYX23M9SVKJ2WQM4PQ7X");
        resource.putLabels(ResourceLabel.DEVICE_NETWORK.getName(), "WIFI");
        resource.putLabels(ResourceLabel.DEVICE_OS.getName(), "30");
        resource.putLabels(ResourceLabel.DEVICE_ROOTED.getName(),  "false");
        resource.putLabels(ResourceLabel.DEVICE_TYPE.getName(), "sdk_gphone_x86");
        resource.putLabels(ResourceLabel.SESSION_ID.getName(), sessionId);

        return resource.build();
    }

    /**
     * Map a List of {@link MetricEntity}s to a List of {@link Metric}s.
     *
     * @param metricEntityList the List of MetricEntity instances.
     * @return the List of Metrics.
     */
    @NonNull
    public static List<Metric> mapMetricEntityList(@NonNull final List<MetricEntity> metricEntityList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return metricEntityList.stream().map(MetricEntity::getMetric).collect(Collectors.toList());
        } else {
            final List<Metric> metricList = new ArrayList<>();
            for (int i = 0; i < metricEntityList.size(); i++) {
                metricList.add(metricEntityList.get(i).getMetric());
            }
            return metricList;
        }
    }

    /**
     * Gets a sample {@link Timestamp} for testing purposes.
     *
     * @param seconds the seconds for the Timestamp.
     * @param nanos   the nanos for the Timestamp.
     * @return the Timestamp.
     */
    @NonNull
    public static Timestamp getTimestamp(final long seconds, final int nanos) {
        final Timestamp.Builder timestampBuilder = Timestamp.newBuilder();
        timestampBuilder.setSeconds(seconds)
                .setNanos(nanos);
        return timestampBuilder.build();
    }


    /**
     * Gets a sample {@link ResourceEntity} for testing purposes.
     *
     * @return the ResourceEntity.
     */
    @NonNull
    public static ResourceEntity getSampleResourceEntity() {
        return new ResourceEntity("sampleLabel", "sampleValue");
    }

    /**
     * Gets an another {@link ResourceEntity} for testing purposes.
     *
     * @return the ResourceEntity.
     */
    @NonNull
    public static ResourceEntity getOtherResourceEntity() {
        return new ResourceEntity("otherLabel", "otherValue");
    }

    /**
     * Gets a different @link ResourceEntity} for testing purposes.
     *
     * @return the ResourceEntity.
     */
    @NonNull
    public static ResourceEntity getDifferentResourceEntity() {
        return new ResourceEntity("differentLabel", "differentValue");
    }

}
