package io.bitrise.trace.data.metric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Objects;
import java.util.UUID;

import io.bitrise.trace.data.storage.TraceDatabase;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.opencensus.proto.metrics.v1.Metric;

/**
 * An {@link Entity} decorator to be able to store {@link Metric} in the {@link TraceDatabase}.
 */
@Entity
public class MetricEntity {

    @TypeConverters(MetricConverter.class)
    @NonNull
    private Metric metric;

    @PrimaryKey
    @NonNull
    private String metricId;

    @NonNull
    private String sessionId;

    private long timeStamp;

    /**
     * Constructor for class.
     *
     * @param metric the {@link Metric}.
     */
    @Ignore
    public MetricEntity(@NonNull final Metric metric) {
        this.metric = metric;
        this.metricId = UUID.randomUUID().toString().substring(0, 16);
        this.sessionId = getCurrentSessionId();
        this.timeStamp = System.currentTimeMillis();
    }

    /**
     * Constructor required for Room.
     *
     * @param metricId the ID of the {@link Metric}.
     */
    public MetricEntity(@NonNull final String metricId) {
        this.metricId = metricId;
    }

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(@NonNull final String metricId) {
        this.metricId = metricId;
    }

    @NonNull
    public Metric getMetric() {
        return metric;
    }

    public void setMetric(@NonNull final Metric metric) {
        this.metric = metric;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @NonNull
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@NonNull final String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MetricEntity)) {
            return false;
        }
        final MetricEntity metricEntity = (MetricEntity) o;
        return metricEntity.metricId.equals(metricId) && metricEntity.sessionId.equals(sessionId) && metricEntity.metric
                .equals(metric);
    }

    /**
     * Gets the active {@link Session}'s ID, or throws IllegalStateException when there is no active Session.
     *
     * @return the Session ID.
     */
    @NonNull
    private String getCurrentSessionId() {
        final Session activeSession = ApplicationSessionManager.getInstance().getActiveSession();
        if (activeSession == null) {
            throw new IllegalStateException("There must be an active Session to create MetricEntities!");
        }
        return activeSession.getUlid();
    }

    @Override
    public String toString() {
        return "MetricEntity{" +
                "metric=" + metric +
                ", metricId='" + metricId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(metric, metricId, sessionId, timeStamp);
    }
}
