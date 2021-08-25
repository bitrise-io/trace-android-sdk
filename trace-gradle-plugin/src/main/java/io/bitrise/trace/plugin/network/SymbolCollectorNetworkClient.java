package io.bitrise.trace.plugin.network;

import androidx.annotation.NonNull;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.bitrise.trace.plugin.configuration.BuildConfigurationManager;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Responsible for initiating and handling network calls with the symbol collector endpoint.
 */
@Singleton
public final class SymbolCollectorNetworkClient {

  /**
   * The base Url for the communication.
   */
  private static final String BASE_URL = "https://symbolcollector.apm.bitrise.io/api/v1/";
  /**
   * {@link Retrofit} instance for network calls.
   */
  private static volatile Retrofit retrofit;
  /**
   * The {@link SymbolCollectorCommunicator} interface of this client.
   */
  private static volatile SymbolCollectorCommunicator symbolCollectorCommunicator;

  /**
   * Gets the client for the {@link SymbolCollectorCommunicator}.
   *
   * @return the client.
   */
  @NonNull
  private static synchronized Retrofit getClient() {
    if (retrofit == null) {
      final OkHttpClient client = getBaseClientBuilder().build();
      retrofit = buildRetrofit(client);
    }
    return retrofit;
  }


  /**
   * Gets a {@link OkHttpClient.Builder} with the common settings for network communications.
   *
   * @return the builder.
   */
  @NonNull
  static OkHttpClient.Builder getBaseClientBuilder() {
    return new OkHttpClient.Builder().addInterceptor(getLoggingInterceptor())
                                     .connectTimeout(15, TimeUnit.SECONDS)
                                     .readTimeout(15, TimeUnit.SECONDS);
  }

  /**
   * Gets the NetworkCommunicator for performing network calls.
   *
   * @return the NetworkCommunicator.
   */
  @NonNull
  public static synchronized SymbolCollectorCommunicator getCommunicator() {
    if (symbolCollectorCommunicator == null) {
      symbolCollectorCommunicator = getClient().create(SymbolCollectorCommunicator.class);
    }
    return symbolCollectorCommunicator;
  }

  /**
   * Builds a {@link Retrofit} with the given {@link OkHttpClient}. Uses a
   * {@link FieldNamingPolicy} to convert JSON field names using LOWER_CASE_WITH_UNDERSCORES.
   * This is required as backend expects this format.
   *
   * @param client the given client.
   * @return the Retrofit.
   */
  @NonNull
  static Retrofit buildRetrofit(@NonNull final OkHttpClient client) {
    return new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(getGson()))
        .client(client)
        .build();
  }

  /**
   * Gets the {@link Interceptor} for logging. WARNING: By default it is turned off, because
   * for OkHttp version 4.3.0 and above the signature of Log method changed, building an app with
   * any of the mentioned versions would lead the SDK to runtime exception. If you want to log
   * the messages change the level of logging manually are recompile the code.
   *
   * @return the {@link HttpLoggingInterceptor}.
   * @see
   * <a href="https://github.com/square/okhttp/blob/parent-4.2.2/okhttp/src/main/java/okhttp3/internal/platform/Platform.kt#L130">https://github.com/square/okhttp/blob/parent-4.2.2/okhttp/src/main/java/okhttp3/internal/platform/Platform.kt#L130</a>
   */
  @NonNull
  private static HttpLoggingInterceptor getLoggingInterceptor() {
    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
    return interceptor;
  }

  /**
   * Gets the current Gson serializer with configured naming strategies and overrides.
   *
   * @return Gson serializer that is used for all network requests.
   */
  @NonNull
  public static Gson getGson() {
    return new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .serializeNulls()
        .create();
  }
}
