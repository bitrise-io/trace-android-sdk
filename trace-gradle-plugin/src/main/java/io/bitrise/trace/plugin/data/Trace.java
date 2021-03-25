package io.bitrise.trace.plugin.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.UUID;

import io.opencensus.proto.trace.v1.Span;

/**
 * Data class for traces.
 */
public class Trace {

    /**
     * The String ID of the Trace.
     */
    @NonNull
    private final String ID;

    /**
     * The {@link Span}s of this Trace.
     */
    @Nullable
    private List<Span> spans;

    /**
     * Constructor for class.
     */
    public Trace() {
        this.ID = UUID.randomUUID().toString().substring(0, 16);
    }

    /**
     * Gets the ID of the Trace.
     *
     * @return the ID.
     */
    @NonNull
    public String getID() {
        return ID;
    }

    /**
     * Gets all the Spans from this Trace.
     *
     * @return the Spans.
     */
    @Nullable
    public List<Span> getSpans() {
        return spans;
    }

    /**
     * Sets the Spans of this Trace.
     *
     * @param spans the List of Spans to set.
     */
    public void setSpans(@Nullable final List<Span> spans) {
        this.spans = spans;
    }
}