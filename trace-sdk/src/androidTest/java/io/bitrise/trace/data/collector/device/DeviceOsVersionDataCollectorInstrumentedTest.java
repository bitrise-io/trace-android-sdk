package io.bitrise.trace.data.collector.device;

import org.junit.Test;

import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented tests for {@link DeviceOsVersionDataCollector}.
 */
public class DeviceOsVersionDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    private final DeviceOsVersionDataCollector collector = new DeviceOsVersionDataCollector();

    /**
     * Verifies that when {@link DeviceOsVersionDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        final String actualValue = (String) collector.collectData().getContent();
        assertThat(actualValue, is(notNullValue()));
    }

    @Test
    public void getPermissions() {
        assertArrayEquals(new String[0], collector.getPermissions());
    }

    @Test
    public void getIntervalMs() {
        assertEquals(0, collector.getIntervalMs());
    }
}
