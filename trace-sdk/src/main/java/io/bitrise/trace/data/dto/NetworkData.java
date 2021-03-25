package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Data class for network calls.
 */
public class NetworkData {

    /**
     * The start time of the network call in milliseconds.
     */
    private long start;
    /**
     * The end time of the network call in milliseconds.
     */
    private long end;
    @NonNull
    private String url;
    private int statusCode;
    @NonNull
    private String method;
    private long requestSize;
    private long responseSize;
    @NonNull
    private final String spanId;
    @NonNull
    private final String parentSpanId;

    /**
     * Constructor for class. Requires an ID for the Span that will be created from it, and the ID of the
     * parent Span.
     *
     * @param spanId       the ID of the Span.
     * @param parentSpanId the ID of the parent Span.
     */
    public NetworkData(@NonNull final String spanId, @NonNull final String parentSpanId) {
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
    }

    @NonNull
    public String getSpanId() {
        return spanId;
    }

    @NonNull
    public String getParentSpanId() {
        return parentSpanId;
    }

    /**
     * Gets the start time of the network call.
     *
     * @return the start time in milliseconds.
     */
    public long getStart() {
        return start;
    }

    /**
     * Sets the start time of the network call.
     *
     * @param start the start time in milliseconds.
     * @return this NetworkData.
     */
    @NonNull
    public NetworkData setStart(final long start) {
        this.start = start;
        return this;
    }

    /**
     * Gets the end time of the network call.
     *
     * @return the end time in milliseconds.
     */
    public long getEnd() {
        return end;
    }

    /**
     * Sets the end time of the network call.
     *
     * @param end the end time in milliseconds.
     * @return this NetworkData.
     */
    @NonNull
    public NetworkData setEnd(final long end) {
        this.end = end;
        return this;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    @NonNull
    public NetworkData setUrl(@NonNull final String url) {
        this.url = url;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @NonNull
    public NetworkData setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @NonNull
    public String getMethod() {
        return method;
    }

    @NonNull
    public NetworkData setMethod(@NonNull final String method) {
        this.method = method;
        return this;
    }

    public long getRequestSize() {
        return requestSize;
    }

    @NonNull
    public NetworkData setRequestSize(final long requestSize) {
        this.requestSize = requestSize;
        return this;
    }

    public long getResponseSize() {
        return responseSize;
    }

    @NonNull
    public NetworkData setResponseSize(final long responseSize) {
        this.responseSize = responseSize;
        return this;
    }

    @Override
    public String toString() {
        return "NetworkData{" +
                "start=" + start +
                ", end=" + end +
                ", url='" + url + '\'' +
                ", statusCode=" + statusCode +
                ", method='" + method + '\'' +
                ", requestSize=" + requestSize +
                ", responseSize=" + responseSize +
                ", spanId='" + spanId + '\'' +
                ", parentSpanId='" + parentSpanId + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkData)) {
            return false;
        }
        final NetworkData that = (NetworkData) o;
        return start == that.start &&
                end == that.end &&
                statusCode == that.statusCode &&
                requestSize == that.requestSize &&
                responseSize == that.responseSize &&
                url.equals(that.url) &&
                method.equals(that.method) &&
                spanId.equals(that.spanId) &&
                parentSpanId.equals(that.parentSpanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, url, statusCode, method, requestSize, responseSize, spanId, parentSpanId);
    }
}
