package io.bitrise.trace;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Instrumented unit test for the {@link TraceSdk} class.
 */
public class TraceSdkInstrumentedTest {

    @Before
    public void setUp() {
        TraceSdk.reset();
    }

    /**
     * Tests that {@link TraceSdk#isInitialised()} should return {@code true}, when the SDK has been initialised.
     */
    @Test
    public void isInitialised_shouldBeTrueAfterInit() {
        TraceSdk.init(ApplicationProvider.getApplicationContext());
        final boolean actualValue = TraceSdk.isInitialised();
        assertThat(actualValue, is(true));
    }

    /**
     * Tests that {@link TraceSdk#isInitialised()} should return {@code false}, when the SDK hasn't been initialised
     * ({@link TraceSdk#init(Context)} not called).
     */
    @Test
    public void isInitialised_shouldBeFalseBeforeInit() {
        final boolean actualValue = TraceSdk.isInitialised();
        assertThat(actualValue, is(false));
    }
}
