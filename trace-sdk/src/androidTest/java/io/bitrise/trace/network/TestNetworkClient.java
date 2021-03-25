package io.bitrise.trace.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import io.bitrise.trace.configuration.ConfigurationManager;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Modified {@link NetworkClient}s for testing purposes.
 */
public class TestNetworkClient {

    /**
     * A sample token that is used for the communication.
     */
    private static final String SAMPLE_TOKEN = "0123456789abcdef";

    /**
     * {@link Retrofit} instance for network calls that always succeeds.
     */
    private static Retrofit successRetrofit;

    /**
     * {@link Retrofit} instance for network calls that always returns failure.
     */
    private static Retrofit failingRetrofit;

    /**
     * {@link Retrofit} instance for network calls that always throw exception.
     */
    private static Retrofit exceptionRetrofit;

    /**
     * The {@link NetworkCommunicator} interface of the {@link #successRetrofit}.
     */
    private static NetworkCommunicator successNetworkCommunicator;

    /**
     * The {@link NetworkCommunicator} interface of the {@link #failingRetrofit}.
     */
    private static NetworkCommunicator failingNetworkCommunicator;

    /**
     * The {@link NetworkCommunicator} interface of the {@link #exceptionRetrofit}.
     */
    private static NetworkCommunicator exceptionNetworkCommunicator;

    /**
     * A mock implementation for the {@link ConfigurationManager}.
     */
    private static ConfigurationManager mockConfigurationManager = mock(ConfigurationManager.class);

    /**
     * Reset all the members to {@code null} value.
     */
    public static void reset() {
        successRetrofit = null;
        failingRetrofit = null;
        exceptionRetrofit = null;

        successNetworkCommunicator = null;
        failingNetworkCommunicator = null;
        exceptionNetworkCommunicator = null;
    }

    /**
     * Gets the NetworkCommunicator for performing network calls that uses the test kit.
     * The {@link ConfigurationManager} should be mocked for the usage and the token for it should be passed as a
     * param. For testing purposes this is not a singleton.
     *
     * @param token the token to use for the communication.
     * @return the NetworkCommunicator.
     */
    @NonNull
    public static NetworkCommunicator getTestKitCommunicator(@NonNull final String token) {
        return getTestKitClient(token).create(NetworkCommunicator.class);
    }

    /**
     * Gets the NetworkCommunicator for performing network calls that will always return success.
     *
     * @return the NetworkCommunicator.
     */
    @NonNull
    public static synchronized NetworkCommunicator getSuccessCommunicator() {
        if (successNetworkCommunicator == null) {
            successNetworkCommunicator = getSuccessClient().create(NetworkCommunicator.class);
        }
        return successNetworkCommunicator;
    }

    /**
     * Gets the NetworkCommunicator for performing network calls that will always return failure.
     *
     * @return the NetworkCommunicator.
     */
    @NonNull
    public static synchronized NetworkCommunicator getFailingCommunicator() {
        if (failingNetworkCommunicator == null) {
            failingNetworkCommunicator = getFailingClient().create(NetworkCommunicator.class);
        }
        return failingNetworkCommunicator;
    }

    /**
     * Gets the NetworkCommunicator for performing network calls that will always throw exception.
     *
     * @return the NetworkCommunicator.
     */
    @NonNull
    public static synchronized NetworkCommunicator getExceptionCommunicator() {
        if (exceptionNetworkCommunicator == null) {
            exceptionNetworkCommunicator = getExceptionClient().create(NetworkCommunicator.class);
        }
        return exceptionNetworkCommunicator;
    }

    /**
     * Gets a test client for the {@link NetworkCommunicator} that uses the test kit. The
     * {@link ConfigurationManager} should be mocked for the usage and the token for it should be passed as a param.
     * The base url for the connection should be 10.0.2.2:9090, as that is the special alias to your host loopback
     * interface. Note: currently it is based on HTTP and not HTTPS. Starting from API level 28 cleartext
     * communication with HTTP will throw an exception.
     *
     * @param token the token to use for the communication.
     * @return the client.
     * @see
     * <a href="https://developer.android.com/studio/run/emulator-networking.html#networkaddresses">https://developer.android.com/studio/run/emulator-networking.html#networkaddresses</a>
     * @see
     * <a href="https://developer.android.com/training/articles/security-config#CleartextTrafficPermitted">https://developer.android.com/training/articles/security-config#CleartextTrafficPermitted</a>
     */
    @NonNull
    private static synchronized Retrofit getTestKitClient(@NonNull final String token) {
        final OkHttpClient client = NetworkClient.getBaseClientBuilder().build();

        updateWithMockMembers(token);
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9090/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    /**
     * Gets a test client for the {@link NetworkCommunicator} that always returns success.
     *
     * @return the client.
     */
    @NonNull
    private static synchronized Retrofit getSuccessClient() {
        if (successRetrofit == null) {
            final OkHttpClient client = NetworkClient.getBaseClientBuilder()
                                                     .addInterceptor(getSuccessInterceptor())
                                                     .build();
            updateWithMockMembers(SAMPLE_TOKEN);
            successRetrofit = NetworkClient.buildRetrofit(client);
        }
        return successRetrofit;
    }

    /**
     * Gets a test client for the {@link NetworkCommunicator} that always returns failure.
     *
     * @return the client.
     */
    @NonNull
    private static synchronized Retrofit getFailingClient() {
        if (failingRetrofit == null) {
            final OkHttpClient client = NetworkClient.getBaseClientBuilder()
                                                     .addInterceptor(getFailingInterceptor())
                                                     .build();
            updateWithMockMembers(SAMPLE_TOKEN);
            failingRetrofit = NetworkClient.buildRetrofit(client);
        }
        return failingRetrofit;
    }

    /**
     * Gets a test client for the {@link NetworkCommunicator} that always returns failure.
     *
     * @return the client.
     */
    @NonNull
    private static synchronized Retrofit getExceptionClient() {
        if (exceptionRetrofit == null) {
            final OkHttpClient client = NetworkClient.getBaseClientBuilder()
                                                     .addInterceptor(getExceptionInterceptor())
                                                     .build();
            updateWithMockMembers(SAMPLE_TOKEN);
            exceptionRetrofit = NetworkClient.buildRetrofit(client);
        }
        return exceptionRetrofit;
    }

    /**
     * Gets the {@link Interceptor} that will make every call return success.
     *
     * @return the {@link Interceptor}.
     */
    @NonNull
    private static Interceptor getSuccessInterceptor() {
        return chain -> {
            final String responseString = "{\"result\": success}";
            return chain.proceed(chain.request())
                        .newBuilder()
                        .code(200)
                        .protocol(Protocol.HTTP_2)
                        .message(responseString)
                        .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();
        };
    }

    /**
     * Gets the {@link Interceptor} that will make every call return failure.
     *
     * @return the {@link Interceptor}.
     */
    @NonNull
    private static Interceptor getFailingInterceptor() {
        return chain -> {
            final String responseString = "{\"result\": failure}";
            return chain.proceed(chain.request())
                        .newBuilder()
                        .code(400)
                        .protocol(Protocol.HTTP_2)
                        .message(responseString)
                        .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                        .addHeader("content-type", "application/json")
                        .build();
        };
    }

    /**
     * Gets the {@link Interceptor} that will make every call throw exception.
     *
     * @return the {@link Interceptor}.
     */
    @NonNull
    private static Interceptor getExceptionInterceptor() {
        return chain -> {
            throw new IOException("Something happened");
        };
    }

    /**
     * Updates the client with the mock members. This is needed as the {@link ConfigurationManager} is cannot be
     * initialised, so we have to mock it.
     *
     * @param token the token to use for the communication.
     */
    private static void updateWithMockMembers(@NonNull final String token) {
        when(mockConfigurationManager.getToken()).thenReturn(token);
        NetworkClient.configurationManager = mockConfigurationManager;
    }
}
