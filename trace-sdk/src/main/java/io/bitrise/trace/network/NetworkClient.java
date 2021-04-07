package io.bitrise.trace.network;

import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.NonNull;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.protobuf.ProtoTypeAdapter;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import io.bitrise.trace.BuildConfig;
import io.bitrise.trace.TraceSdk;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.network.adapters.ByteStringAdapter;
import io.bitrise.trace.network.adapters.PointAdapter;
import io.bitrise.trace.network.adapters.ResourceAdapter;
import io.bitrise.trace.network.adapters.SpanAttributeAdapter;
import io.opencensus.proto.metrics.v1.Point;
import io.opencensus.proto.resource.v1.Resource;
import io.opencensus.proto.trace.v1.Span;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Responsible for initiating and handling network calls.
 */
@Singleton
public final class NetworkClient {

    /**
     * The base Url for the communication.
     */
    private static final String BASE_URL = "https://collector.apm.bitrise.io";

    /**
     * The maximum number of languages that can be sent in the header of a request.
     */
    static final int MAXIMUM_NUMBER_OF_LANGUAGES = 6;

    /**
     * {@link Retrofit} instance for network calls.
     */
    private static volatile Retrofit retrofit;

    /**
     * The {@link NetworkCommunicator} interface of this client.
     */
    private static volatile NetworkCommunicator networkCommunicator;

    /**
     * The {@link ConfigurationManager}.
     */
    static ConfigurationManager configurationManager;

    /**
     * Gets the client for the {@link NetworkCommunicator}.
     *
     * @return the client.
     */
    @NonNull
    private static synchronized Retrofit getClient() {
        if (retrofit == null) {
            final OkHttpClient client = getBaseClientBuilder().build();
            retrofit = buildRetrofit(client);
            configurationManager = ConfigurationManager.getInstance();
        }
        return retrofit;
    }

    /**
     * Constructor to prevent instantiation outside of the class.
     */
    private NetworkClient() {
        // nop
    }

    /**
     * Gets a {@link OkHttpClient.Builder} with the common settings for network communications.
     *
     * @return the builder.
     */
    @NonNull
    static OkHttpClient.Builder getBaseClientBuilder() {
        return new OkHttpClient.Builder().addInterceptor(getLoggingInterceptor())
                                         .addInterceptor(getCallInterceptor())
                                         .connectTimeout(15, TimeUnit.SECONDS)
                                         .readTimeout(15, TimeUnit.SECONDS);
    }

    /**
     * Gets the NetworkCommunicator for performing network calls.
     *
     * @return the NetworkCommunicator.
     */
    @NonNull
    static synchronized NetworkCommunicator getCommunicator() {
        if (networkCommunicator == null) {
            networkCommunicator = getClient().create(NetworkCommunicator.class);
        }
        return networkCommunicator;
    }

    /**
     * Gets the current Gson serializer with configured naming strategies and overrides.
     * @return Gson serializer that is used for all network requests.
     */
    @NonNull
    public static Gson getGson() {
        return new GsonBuilder()
                .setFieldNamingStrategy(new SnakeCaseStrategy())
                .registerTypeHierarchyAdapter(ByteString.class, new ByteStringAdapter())
                .registerTypeAdapter(Point.class, new PointAdapter())
                .registerTypeAdapter(Span.Attributes.class, new SpanAttributeAdapter())
                .registerTypeAdapter(Resource.class, new ResourceAdapter())
                .registerTypeHierarchyAdapter(GeneratedMessageV3.class,
                        ProtoTypeAdapter.newBuilder()
                                .setFieldNameSerializationFormat(CaseFormat.LOWER_UNDERSCORE, CaseFormat.LOWER_UNDERSCORE)
                                .setEnumSerialization(ProtoTypeAdapter.EnumSerialization.NUMBER)
                                .build())
                .serializeNulls()
                .create();
    }

    /**
     * Builds a {@link Retrofit} with the given {@link OkHttpClient}. Uses a {@link SnakeCaseStrategy} to convert
     * JSON field names using snake case. This is required as backend does the same.
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
     * Gets the {@link Interceptor} for logging. WARNING: By default it is turned off, because for OkHttp version 4.3.0
     * and above the signature of Log method changed, building an app with any of the mentioned versions would lead the
     * SDK to runtime exception. If you want to log the messages change the level of logging manually are recompile
     * the code.
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
     * Gets the user agent for the HTTP request headers.
     *
     * @return the value for user agent.
     */
    @NonNull
    static String getUserAgent() {
        return String.format("%1$s/%2$s: %3$s", TraceSdk.COMPANY, TraceSdk.NAME, BuildConfig.VERSION_NAME);
    }

    /**
     * Gets the content length for a given request.
     *
     * @param chain the {@link okhttp3.Interceptor.Chain} which has the request.
     * @return the String value of the length.
     * @throws IOException if any IO errors occur.
     */
    @NonNull
    static String getContentLength(@NonNull final Interceptor.Chain chain) throws IOException {
        final RequestBody requestBody = chain.request().body();
        final long contentLength = requestBody == null ? 0L : requestBody.contentLength();
        return String.valueOf(contentLength);
    }

    /**
     * Gets the authorization bearer token for the header.
     *
     * @return the token.
     */
    @NonNull
    private static String getAuthorizationBearer() {
        return String.format("Bearer %1$s", configurationManager.getToken());
    }

    /**
     * Gets the Interceptor fot the network calls. This is used to add the header to every request.
     *
     * @return the Interceptor.
     */
    @NonNull
    private static Interceptor getCallInterceptor() {
        return chain -> {
            final Request request =
                    chain.request().newBuilder()
                         .addHeader("Authorization", getAuthorizationBearer())
                         .addHeader("Accept-Encoding", "gzip;q=1.0, compress;q=0.5)")
                         .addHeader("Accept-Language", getAcceptedLanguage())
                         .addHeader("Connection", "keep-alive")
                         .addHeader("Content-Length", getContentLength(chain))
                         .addHeader("User-Agent", getUserAgent())
                         .build();
            return chain.proceed(request);
        };
    }

    /**
     * Gets the accepted language for the HTTP headers. The number of languages is limited in
     * {@link #MAXIMUM_NUMBER_OF_LANGUAGES}.
     *
     * @return the value for accepted language.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language">Accept-Language</a>
     * @see <a href="https://developer.mozilla.org/en-US/docs/Glossary/quality_values">Quality values</a>
     */
    @NonNull
    static String getAcceptedLanguage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final List<Locale> locales = new ArrayList<>();
            final LocaleList localeList = LocaleList.getDefault();
            final int length = Math.min(localeList.size(), MAXIMUM_NUMBER_OF_LANGUAGES);
            for (int i = 0; i < length; i++) {
                locales.add(localeList.get(i));
            }
            return formatLanguageWithPriority(locales.toArray(new Locale[]{}));
        } else {
            return formatLanguageWithPriority(Locale.getDefault());
        }
    }

    /**
     * Formats the given {@link Locale}s with the priority quality values. Multiple values will
     * be separated with a comma.
     *
     * @param locales the Locale(s) to format.
     * @return the formatted language tag with priority.
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language">Accept-Language</a>
     * * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/quality_values">Quality values</a>
     */
    @NonNull
    static String formatLanguageWithPriority(@NonNull final Locale... locales) {
        final StringBuilder stringBuilder = new StringBuilder();
        final int length = locales.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(String.format("%1$s;q=%2$s", locales[i].getLanguage(),
                    ((double) 1) - 0.1 * (double) i));
            if (i != length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
