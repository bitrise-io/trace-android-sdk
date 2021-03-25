package io.bitrise.trace.configuration;

/**
 * Holds the build configuration related constants.
 */
public class ConfigurationConstants {

    /**
     * The key for the token in the BuildConfig file.
     */
    public static final String BITRISE_BC_TOKEN_KEY = "BITRISE_TRACE_TOKEN";

    /**
     * The key for the package name of the BuildConfig file.
     */
    public static final String BITRISE_BC_PACKAGE_NAME_KEY = "bitrise_package_name";

    private ConfigurationConstants() {
        throw new IllegalStateException("Should not be instantiated, used only for storing static members!");
    }
}
