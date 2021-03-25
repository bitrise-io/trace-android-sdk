package io.bitrise.trace.plugin.task;

/**
 * Contains the configuration for the tasks.
 */
public class TaskConfig {

    private TaskConfig() {
        throw new IllegalStateException("Should not be instantiated, used only for storing static members!");
    }

    /**
     * The group name for Trace tasks.
     */
    public static final String TRACE_PLUGIN_TASK_GROUP = "Trace";

    /**
     * The task name for {@link ManifestModifierTask}.
     */
    public static final String TASK_NAME_MANIFEST_MODIFIER = "modifyManifest";

    /**
     * The description for {@link ManifestModifierTask}.
     */
    public static final String TASK_DESCRIPTION_MANIFEST_MODIFIER = "Modifies the AndroidManifest" +
            ".xml. Adds the Trace application if no other custom application class is set.";

    /**
     * The task name for {@link GenerateBuildIDTask}.
     */
    public static final String TASK_NAME_GENERATE_BUILD_ID = "generateBitriseBuildId";

    /**
     * The description for {@link GenerateBuildIDTask}.
     */
    public static final String TASK_DESCRIPTION_GENERATE_BUILD_ID = "Creates a unique build ID for the given build.";

    /**
     * The task name for {@link VerifyTraceTask}.
     */
    public static final String TASK_NAME_VERIFY_TRACE = "verifyTrace";

    /**
     * The description for {@link VerifyTraceTask}.
     */
    public static final String TASK_DESCRIPTION_VERIFY_TRACE = "Verifies that the Trace Plugin and SDK is correctly " +
            "added to the project.";
}
