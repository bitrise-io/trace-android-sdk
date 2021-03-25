package io.bitrise.trace.data.dto;

import androidx.annotation.NonNull;

import io.bitrise.trace.utils.TraceClock;

/**
 * Data class for a single lifecycle change event for a Fragment.
 */
public class FragmentDataStateEntry {

    /**
     * The {@link FragmentState} of the given event.
     */
    @NonNull
    private FragmentState fragmentState;
    private long timeStamp;

    /**
     * Constructor for class.
     *
     * @param fragmentState the state of the change.
     * @param timeStamp     the timestamp when this happened. Preferably use {@link TraceClock} for getting it.
     */
    public FragmentDataStateEntry(@NonNull final FragmentState fragmentState, final long timeStamp) {
        this.fragmentState = fragmentState;
        this.timeStamp = timeStamp;
    }

    @NonNull
    public FragmentState getFragmentState() {
        return fragmentState;
    }

    public void setFragmentState(@NonNull final FragmentState fragmentState) {
        this.fragmentState = fragmentState;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "FragmentDataStateEntry{" +
                "fragmentState=" + fragmentState +
                ", timeStamp=" + timeStamp +
                '}';
    }

    /**
     * Comparator for this class, compares them by their {@link #timeStamp}.
     */
    public static class Comparator implements java.util.Comparator<FragmentDataStateEntry> {

        @Override
        public int compare(@NonNull final FragmentDataStateEntry o1, @NonNull final FragmentDataStateEntry o2) {
            return Long.compare(o1.timeStamp, o2.timeStamp);
        }
    }
}
