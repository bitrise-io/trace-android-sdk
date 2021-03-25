package io.bitrise.trace.testapp.network;

import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/**
 * Activity that will make URLConnection network type calls.
 */
public class UrlConnectionActivity extends BaseNetworkActivity {

    @NonNull
    @Override
    protected String getTitleContent() {
        return "URLConnection";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnConnectHttp.setOnClickListener(v -> sendHttpRequest());
        btnConnectHttps.setOnClickListener(v -> sendHttpsRequest());
    }

    /**
     * Creates a String representation of an InputStream.
     *
     * @param inputStream the inputStream from the URLConnection.
     * @return the String representation of the network response.
     * @throws IOException if there is something wrong with the InputStream.
     */
    @NonNull
    private static String readInputStream(@NonNull final InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        int charsRead;
        while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
            out.append(buffer, 0, charsRead);
        }
        return out.toString();
    }

    @Override
    protected void sendHttpRequest() {
        btnConnectHttp.setEnabled(false);
        btnConnectHttps.setEnabled(false);
        TestAppCountingIdlingResource.increment();

        new Thread(() -> {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(httpEndpoint);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setInstanceFollowRedirects(false);
                urlConnection.connect();
                displayResponse(
                        urlConnection.getResponseCode(),
                        readInputStream(urlConnection.getInputStream()));
            } catch (final Exception e) {
                displayError(e.getLocalizedMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                TestAppCountingIdlingResource.decrement();
            }
        }).start();
    }

    @Override
    protected void sendHttpsRequest() {
        btnConnectHttp.setEnabled(false);
        btnConnectHttps.setEnabled(false);
        TestAppCountingIdlingResource.increment();

        new Thread(() -> {
            URL url;
            HttpsURLConnection urlConnection = null;
            try {
                url = new URL(httpsEndpoint);
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.connect();
                displayResponse(
                        urlConnection.getResponseCode(),
                        readInputStream(urlConnection.getInputStream()));
            } catch (final Exception e) {
                displayError(e.getLocalizedMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                TestAppCountingIdlingResource.decrement();
            }
        }).start();
    }
}