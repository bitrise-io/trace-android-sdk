package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;

/**
 * TraceExceptions are used to express and describe some kind of business type exceptions.
 */
public class TraceException {

    /**
     * Used when the TraceConfig file is not found in the correct location.
     */
    public static class TraceConfigFileNotFoundException extends IllegalStateException {
        public TraceConfigFileNotFoundException(@NonNull String fileName, @NonNull String rootDirectory) {
            super(String.format("Could not find the JSON configuration file in project root" +
                    ". Please make sure %1$s can be found in %2$s", fileName, rootDirectory));
        }
    }

    /**
     * Used when the TraceConfig file has an issue being read.
     */
    public static class TraceConfigFileInvalidException extends IllegalStateException {
        public TraceConfigFileInvalidException(@NonNull String fileName, @NonNull String errorMessage) {
            super(String.format("Invalid %1$s : %2$s", fileName, errorMessage ));
        }
    }
}
