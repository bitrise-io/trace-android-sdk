package io.bitrise.trace.testapp.network;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Activity that will do OkHttp network calls.
 */
public class OkHttpActivity extends BaseNetworkActivity {

    @Override
    @NonNull
    protected String getTitleContent() {
        return "OkHttp";
    }

    @Override
    protected void sendHttpRequest() {
        enableSending(false);
        clearTexts();
        executeRequest(createRequest(httpEndpoint));
    }

    @Override
    protected void sendHttpsRequest() {
        enableSending(false);
        clearTexts();
        executeRequest(createRequest(httpsEndpoint));
    }

    /**
     * Creates a {@link Request} from the given endpoint value.
     *
     * @param endpoint the String value of the endpoint's url.
     * @return the created Request.
     */
    @NonNull
    private Request createRequest(@NonNull final String endpoint) {
        return new Request.Builder().url(endpoint).build();
    }

    /**
     * Creates an {@link OkHttpClient} for the network calls.
     *
     * @return the created OkHttpClient.
     */
    @NonNull
    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder().followRedirects(false).build();
    }

    /**
     * Executes the given {@link Request}.
     *
     * @param request the Request to execute.
     */
    private void executeRequest(@NonNull final Request request) {
        final OkHttpClient okHttpClient = createOkHttpClient();
        final Callback callback = createCallback();
        Executors.newSingleThreadExecutor().execute(() -> okHttpClient.newCall(request).enqueue(callback));
    }

    /**
     * Creates a {@link Callback} for handling the response of the network calls.
     *
     * @return the created Callback.
     */
    private Callback createCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                displayError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                final ResponseBody responseBody = response.body();
                displayResponse(response.code(), responseBody == null ? "" : responseBody.string());
            }
        };
    }
}
