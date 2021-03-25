package io.bitrise.trace.testapp.network;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.bitrise.trace.testapp.R;

/**
 * Base common class for networking related Activities, such as OkHttp, UrlStreamHandler.
 */
public abstract class BaseNetworkActivity extends AppCompatActivity {

    protected String httpEndpoint = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_hour.geojson";
    protected String httpsEndpoint = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_hour" +
            ".geojson";

    protected TextView lblTitle;
    protected Button btnConnectHttp;
    protected Button btnConnectHttps;
    protected TextView lblResponseCode;
    protected TextView lblResponseBody;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_network);
        lblTitle = findViewById(R.id.lbl_title);
        btnConnectHttp = findViewById(R.id.btn_connect_http);
        btnConnectHttps = findViewById(R.id.btn_connect_https);
        lblResponseCode = findViewById(R.id.lbl_response_status_code);
        lblResponseBody = findViewById(R.id.lbl_response_body);
        initViews();
    }

    /**
     * Initialises the views.
     */
    protected void initViews() {
        lblTitle.setText(getTitleContent());
        btnConnectHttp.setOnClickListener(v -> sendHttpRequest());
        btnConnectHttps.setOnClickListener(v -> sendHttpsRequest());
    }

    /**
     * Displays an error on the screen by clearing the response code, and setting the body to the error message.
     *
     * @param error the error to display.
     */
    protected void displayError(@NonNull final String error) {
        runOnUiThread(() -> {
            enableSending(true);
            lblResponseCode.setText("");
            lblResponseBody.setText(error);
        });
    }

    /**
     * Displays the response on the screen, by setting the response code and body TextViews.
     *
     * @param statusCode the HTTP status code to display.
     * @param response   the body of the response.
     */
    @SuppressLint("SetTextI18n")
    protected void displayResponse(final int statusCode, @NonNull final String response) {
        runOnUiThread(() -> {
            enableSending(true);
            lblResponseCode.setText(Integer.toString(statusCode));
            lblResponseBody.setText(response);
        });
    }

    /**
     * Enables the sending by enabling clicking on buttons {@link #btnConnectHttp} and {@link #btnConnectHttps}.
     *
     * @param state {@code true} to enable, {@code false} otherwise.
     */
    protected void enableSending(final boolean state) {
        btnConnectHttp.setEnabled(state);
        btnConnectHttps.setEnabled(state);
    }

    /**
     * Clears the text from {@link #lblResponseBody} and {@link #lblResponseCode}.
     */
    protected void clearTexts() {
        lblResponseBody.setText("");
        lblResponseCode.setText("");
    }

    /**
     * Gets the title content for {@link #lblTitle}. The title should be the given networking library's name.
     *
     * @return the title.
     */
    @NonNull
    protected abstract String getTitleContent();

    /**
     * Executes the sending of an HTTP request.
     */
    protected abstract void sendHttpRequest();

    /**
     * Executes the sending of an HTTPS request.
     */
    protected abstract void sendHttpsRequest();
}