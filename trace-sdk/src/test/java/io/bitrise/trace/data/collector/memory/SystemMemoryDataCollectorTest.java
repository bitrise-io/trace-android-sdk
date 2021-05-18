package io.bitrise.trace.data.collector.memory;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link SystemMemoryDataCollector}.
 */
public class SystemMemoryDataCollectorTest {

    private final Context mockContext = Mockito.mock(Context.class);
    private final SystemMemoryDataCollector collector =
            new SystemMemoryDataCollector(mockContext);

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(15000, collector.getIntervalMs());
    }

}
