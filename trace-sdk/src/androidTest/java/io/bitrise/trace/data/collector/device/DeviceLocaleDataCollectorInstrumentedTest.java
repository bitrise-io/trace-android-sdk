package io.bitrise.trace.data.collector.device;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;

import io.bitrise.trace.data.collector.BaseDataCollectorInstrumentedTest;
import io.bitrise.trace.data.dto.Data;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Instrumented tests for {@link DeviceLocaleDataCollector}.
 */
public class DeviceLocaleDataCollectorInstrumentedTest extends BaseDataCollectorInstrumentedTest {

    final DeviceLocaleDataCollector collector = new DeviceLocaleDataCollector(context);

    /**
     * Verifies that when {@link DeviceLocaleDataCollector#collectData()} is called, the content of the returned
     * {@link Data} should not be {@code null}.
     */
    @Test
    public void collectData_contentShouldBeNotNull() {
        assertThat(collector.collectData().getContent(), is(notNullValue()));
    }

}
