package io.bitrise.trace.testapp.ui;

import android.app.Application;
import android.util.Log;

/**
 * Custom application class for testing purposes.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyApplication", "Application Started");
    }
}
