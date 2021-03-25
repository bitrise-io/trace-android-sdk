package io.bitrise.trace.network;

import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

import io.bitrise.trace.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contains the unit tests for the {@link NetworkClient} class.
 */
public class NetworkClientTest {

    private static final Locale englishLocale = new Locale("en");
    private static final Locale hungarianLocale = new Locale("hu");
    private static final Locale japaneseLocale = new Locale("ja");

    /**
     * Checks that the name of the company and the SDK have not changed.
     */
    @Test
    public void getUserAgent_nameShouldNotChange() {
        final String expectedValue = "Bitrise/Trace Android: " + BuildConfig.VERSION_NAME;
        final String actualValue = NetworkClient.getUserAgent();
        assertThat(actualValue, is(expectedValue));
    }

    /**
     * Checks that the result of the {@link NetworkClient#getContentLength(Interceptor.Chain)}
     * should be equal with the value of {@link RequestBody#contentLength()} in the
     * {@link Request} in the given  {@link okhttp3.Interceptor.Chain}.
     *
     * @throws IOException if any IO errors occur.
     */
    @Test
    public void getContentLength_shouldReturnValue() throws IOException {
        final long expectedValue = 5;

        final Interceptor.Chain mockChain = mock(Interceptor.Chain.class);
        final RequestBody mockRequestBody = mock(RequestBody.class);
        when(mockRequestBody.contentLength()).thenReturn(expectedValue);
        final Request request = new Request.Builder().put(mockRequestBody)
                                                     .url("http://bitrise.io")
                                                     .build();
        when(mockChain.request()).thenReturn(request);
        final String actualValue = NetworkClient.getContentLength(mockChain);

        assertThat(actualValue, is(String.valueOf(expectedValue)));
    }

    /**
     * Checks that the result of the {@link NetworkClient#getContentLength(Interceptor.Chain)} is 0,
     * when content body is {@code null} of the given {@link Request}.
     *
     * @throws IOException if any IO errors occur.
     */
    @Test
    public void getContentLength_nullBodyShouldReturnZero() throws IOException {
        final Interceptor.Chain mockChain = mock(Interceptor.Chain.class);
        final Request request = new Request.Builder().url("http://bitrise.io").build();
        when(mockChain.request()).thenReturn(request);
        final String actualValue = NetworkClient.getContentLength(mockChain);
        assertThat(String.valueOf(0L), is(actualValue));
    }

    /**
     * Checks that {@link NetworkClient#formatLanguageWithPriority(Locale...)} when only one
     * Locale is present the priority should be '1.0'.
     */
    @Test
    public void formatLanguageWithPriority_singleLocaleShouldHavePriorityOfOne() {
        final String expectedValue = "en;q=1.0";

        final String actualValue = NetworkClient.formatLanguageWithPriority(englishLocale);

        assertThat(actualValue, is(expectedValue));
    }

    /**
     * Checks that {@link NetworkClient#formatLanguageWithPriority(Locale...)} when multiple
     * Locale is present the priority should be decreasing with '0.1' for each Locale.
     */
    @Test
    public void formatLanguageWithPriority_multiLocaleShouldHaveDecreasingPriority() {
        final String expectedValue = "en;q=1.0,hu;q=0.9,ja;q=0.8";
        final String actualValue = NetworkClient.formatLanguageWithPriority(
                englishLocale,
                hungarianLocale,
                japaneseLocale);

        assertThat(actualValue, is(expectedValue));
    }
}