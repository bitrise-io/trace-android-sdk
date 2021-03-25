package io.bitrise.trace.data.trace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import io.bitrise.trace.session.Session;

/**
 * Wrapper {@link Entity} class for {@link Trace}.
 */
@Entity
public class TraceEntity {

    /**
     * The String ID of the Trace.
     */
    @NonNull
    @PrimaryKey
    private String traceId;
    /**
     * The {@link Trace} object that this entity holds.
     */
    @TypeConverters(TraceConverter.class)
    @NonNull
    private Trace trace;

    private long timeStamp;

    /**
     * The ID of the {@link Session}.
     */
    @NonNull
    private String sessionId;

    /**
     * Constructor for class. Used by Room
     */
    public TraceEntity() {
        // nop
    }

    /**
     * Constructor for class. Used to create a Trace object
     *
     * @param trace the {@link Trace} of the given Entity.
     */
    @Ignore
    public TraceEntity(@NonNull final Trace trace) {
        this.trace = trace;
        this.traceId = trace.getTraceId();
        this.sessionId = trace.getSessionId();
        this.timeStamp = System.currentTimeMillis();
    }

    @NonNull
    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(@NonNull final String traceId) {
        this.traceId = traceId;
    }

    @NonNull
    public Trace getTrace() {
        return trace;
    }

    public void setTrace(@NonNull final Trace trace) {
        this.trace = trace;
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
        if (!(o instanceof TraceEntity)) {
            return false;
        }
        final TraceEntity traceEntity = (TraceEntity) o;
        return traceEntity.trace.equals(this.trace);
    }

    @Override
    public String toString() {
        return "TraceEntity{" +
                "traceId='" + traceId + '\'' +
                ", trace=" + trace +
                ", timeStamp=" + timeStamp +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}