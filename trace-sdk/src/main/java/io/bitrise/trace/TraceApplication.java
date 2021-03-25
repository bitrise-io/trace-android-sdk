package io.bitrise.trace;

import android.app.Application;

import javax.inject.Singleton;

/**
 * Custom Application class to manage Trace services, such as data collection or session management.
 */
@Singleton
public class TraceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TraceSdk.init(getApplicationContext());
    }
}