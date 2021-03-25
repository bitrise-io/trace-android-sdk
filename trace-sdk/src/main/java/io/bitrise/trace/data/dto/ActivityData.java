package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import io.bitrise.trace.utils.TraceClock;

/**
 * Data class for the required data that we want to extract from Activities. It should contain the following data:
 * <ul>
 *     <li>lifecycle events that the given Activity reached, to be able to identify the start and the end of it</li>
 *     <li>the name of the Activity to be able to provide a human readable title for it on the frontend</li>
 *     <li>the Span ID of the Activity</li>
 * </ul>
 * <p>
 * Example format:
 * ActivityData{stateMap={CREATED=1191562272, STARTED=1191562450, STOPPED=1191563000}, name='MainActivity',
 * spanId='ABCDEFGHIJKLMNOP'}
 */
public class ActivityData {

    /**
     * A Map that stores the {@link ActivityState}s with the long value of current time on the device, when the states
     * have been reached. Use {@link TraceClock#getCurrentTimeMillis()} for providing the timestamps.
     */
    @NonNull
    private Map<ActivityState, Long> stateMap;

    /**
     * The name of the Activity.
     */
    @Nullable
    private String name;

    /**
     * The Span ID of the Activity.
     */
    @NonNull
    private final String spanId;

    /**
     * Constructor for class.
     *
     * @param spanId the ID of the Span that will be created from this.
     */
    public ActivityData(@NonNull final String spanId) {
        this.stateMap = new HashMap<>();
        this.spanId = spanId;
    }

    @NonNull
    public Map<ActivityState, Long> getStateMap() {
        return stateMap;
    }

    public void setStateMap(@NonNull final Map<ActivityState, Long> stateMap) {
        this.stateMap = stateMap;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable final String name) {
        this.name = name;
    }

    @NonNull
    public String getSpanId() {
        return spanId;
    }

    /**
     * Puts the given {@link ActivityState} to the {@link #stateMap}.
     *
     * @param activityState the ActivityState to put.
     * @param time          the time when the ActivityState was reached. See more info in the javadoc of
     *                      {@link #stateMap}.
     */
    public void putState(@NonNull final ActivityState activityState, @NonNull final Long time) {
        stateMap.put(activityState, time);
    }

    @Override
    public String toString() {
        return "ActivityData{" +
                "stateMap=" + stateMap +
                ", name='" + name + '\'' +
                ", spanId='" + spanId + '\'' +
                '}';
    }
}
