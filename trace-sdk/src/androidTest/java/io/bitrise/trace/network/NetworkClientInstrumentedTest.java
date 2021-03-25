package io.bitrise.trace.network;

import android.os.Build;
import android.os.LocaleList;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Locale;

import io.bitrise.trace.InstrumentedTestRequirements;
import io.bitrise.trace.TestKitTest;
import io.bitrise.trace.TestKitUtils;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.MetricTestProvider;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Contains the instrumented tests for {@link NetworkClient} class.
 */
public class NetworkClientInstrumentedTest {

    private static final Locale englishLocale = new Locale("en");
    private static final Locale hungarianLocale = new Locale("hu");
    private static final Locale japaneseLocale = new Locale("ja");
    private static final Locale germanLocale = new Locale("de");
    private static final Locale italianLocale = new Locale("it");
    private static final Locale spanishLocale = new Locale("es");
    private static final Locale polishLocale = new Locale("pl");

    /**
     * Checks that {@link NetworkClient#getAcceptedLanguage()} when more than the
     * {@link NetworkClient#MAXIMUM_NUMBER_OF_LANGUAGES} locale is present, then the return value
     * should not contain more.
     */
    @Test
    public void getAcceptedLanguage_numberOfLocaleShouldBeLimitedToSix() {
        String expectedValue = "en;q=1.0,hu;q=0.9,ja;q=0.8,de;q=0.7,it;q=0.6,es;q=0.5";

        // Android 7+ allows multiple languages in priority order.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.setDefault(
                    new LocaleList(englishLocale, hungarianLocale, japaneseLocale, germanLocale,
                            italianLocale, spanishLocale, polishLocale));
        } else {
            // Android 6 only allows 1 language
            expectedValue = Locale.getDefault().getLanguage() + ";q=1.0";
        }

        final String actualValue = NetworkClient.getAcceptedLanguage();

        assertThat(actualValue, is(expectedValue));
    }

    /**
     * Checks that the {@link NetworkClient#getCommunicator()} returns a non null value.
     */
    @Test
    public void getCommunicator_assertNotNull() {
        final NetworkCommunicator actualValue = NetworkClient.getCommunicator();
        assertThat(actualValue, is(notNullValue()));
    }

    /**
     * Checks that the {@link NetworkClient#getCommunicator()} returns a Singleton value.
     */
    @Test
    public void getCommunicator_assertIsSingleton() {
        final NetworkCommunicator expectedValue = NetworkClient.getCommunicator();
        final NetworkCommunicator actualValue = NetworkClient.getCommunicator();
        assertThat(actualValue, sameInstance(expectedValue));
    }

    /**
     * Tests the authentication with a valid token. Result code should not be 401. Currently it is 422, as the server
     * cannot parse the entity.
     * <p>
     * This test requires a test kit docker image to run. See root README.md for more details.
     *
     * @throws IOException if any I/O exception occurs.
     */
    @TestKitTest
    @Test
    public void testKit_sendMetricWithValidToken() throws IOException {
        InstrumentedTestRequirements.assumeTestKitApiLevel();

        final NetworkCommunicator networkCommunicator = TestKitUtils.getNetworkCommunicatorWithValidToken();
        final MetricRequest metricRequest =
                DataTestUtils.getMetricRequest(Collections.singletonList(MetricTestProvider.getEmptyMetric()));
        final Response<Void> response =
                networkCommunicator.sendMetrics(metricRequest).execute();
        final int actualValue = response.code();
        assertThat(actualValue, not(HttpURLConnection.HTTP_UNAUTHORIZED));
    }

    /**
     * Tests the authentication with an unexpected token. Result code should be 500, this is a predefined response for
     * this token for testing purposes.
     * <p>
     * This test requires a test kit docker image to run. See root README.md for more details.
     *
     * @throws IOException if any I/O exception occurs.
     */
    @TestKitTest
    @Test
    public void testKit_sendMetricWithUnexpectedToken() throws IOException {
        InstrumentedTestRequirements.assumeTestKitApiLevel();

        final NetworkCommunicator networkCommunicator = TestKitUtils.getNetworkCommunicatorWithUnexpectedToken();
        final MetricRequest metricRequest =
                DataTestUtils.getMetricRequest(Collections.singletonList(MetricTestProvider.getEmptyMetric()));
        final Response<Void> response =
                networkCommunicator.sendMetrics(metricRequest).execute();
        final int actualValue = response.code();
        assertThat(actualValue, is(HttpURLConnection.HTTP_INTERNAL_ERROR));
    }

    /**
     * Tests the authentication with an invalid token. Result code should be 401.
     * <p>
     * This test requires a test kit docker image to run. See root README.md for more details.
     *
     * @throws IOException if any I/O exception occurs.
     */
    @TestKitTest
    @Test
    public void testKit_sendMetricWithInvalidToken() throws IOException {
        InstrumentedTestRequirements.assumeTestKitApiLevel();

        final NetworkCommunicator networkCommunicator = TestKitUtils.getNetworkCommunicatorWithInvalidToken();
        final MetricRequest metricRequest =
                DataTestUtils.getMetricRequest(Collections.singletonList(MetricTestProvider.getEmptyMetric()));
        final Response<Void> response =
                networkCommunicator.sendMetrics(metricRequest).execute();
        final int actualValue = response.code();
        assertThat(actualValue, is(HttpURLConnection.HTTP_UNAUTHORIZED));
    }
}
