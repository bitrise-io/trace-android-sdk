package io.bitrise.trace.data.collector;

import androidx.annotation.NonNull;

import java.util.Objects;

import io.bitrise.trace.data.dto.Data;

/**
 * Dummy implementation of DataListener for testing purposes.
 */
public class DummyDataListener extends BaseDataListener {

    /**
     * A name for the DummyDataListener. Used to test different and same type behaviours.
     */
    @NonNull
    private final String name;

    /**
     * Constructor for class.
     *
     * @param name the name for this instance.
     */
    public DummyDataListener(@NonNull final String name) {
        this.name = name;
    }

    @Override
    public void startCollecting() {
        // nop
    }

    @Override
    public void stopCollecting() {
        // nop
    }

    @Override
    public void onDataCollected(@NonNull Data data) {
        // nop
    }

    @NonNull
    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DummyDataListener)) {
            return false;
        }
        final DummyDataListener dummyDataListener = (DummyDataListener) o;
        return Objects.equals(name, dummyDataListener.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
