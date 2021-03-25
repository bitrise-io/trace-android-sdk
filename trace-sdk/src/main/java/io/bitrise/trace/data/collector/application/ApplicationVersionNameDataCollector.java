package io.bitrise.trace.data.collector.application;

import android.content.Context;

import androidx.annotation.NonNull;

import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * {@link DataCollector} type, that collects version name of the Application.
 */
public class ApplicationVersionNameDataCollector extends ApplicationDataCollector {

    @NonNull
    private final Context context;

    /**
     * Constructor for class.
     *
     * @param context the Android Context.
     */
    public ApplicationVersionNameDataCollector(@NonNull final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Data collectData() {
        final Data data = new Data(this);
        data.setContent(getAppVersionName());
        return data;
    }

    @NonNull
    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    /**
     * Gets the version name of the Application.
     *
     * @return the version name.
     */
    @NonNull
    private String getAppVersionName() {
        if (!ConfigurationManager.isInitialised()) {
            ConfigurationManager.init(context);
        }
        return (String) ConfigurationManager.getInstance().getConfigItem("VERSION_NAME");
    }
}
