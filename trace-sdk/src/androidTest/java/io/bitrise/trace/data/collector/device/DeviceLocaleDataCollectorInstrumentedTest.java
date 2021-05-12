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
import static org.junit.Assert.assertArrayEquals;
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

    @Test
    public void collectData_mockData() {
        final String locale = "en";
        final Context mockContext = Mockito.mock(Context.class, Mockito.CALLS_REAL_METHODS );
        final DeviceLocaleDataCollector deviceLocaleDataCollector = new DeviceLocaleDataCollector(mockContext);

        final Configuration configuration = new Configuration();
        configuration.setLocale(new Locale(locale));

        final Resources mockResources = Mockito.mock(Resources.class);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getConfiguration())
                .thenReturn(configuration);

        final Data expectedData = new Data(DeviceLocaleDataCollector.class);
        expectedData.setContent(locale);

        assertEquals(expectedData, deviceLocaleDataCollector.collectData());
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
