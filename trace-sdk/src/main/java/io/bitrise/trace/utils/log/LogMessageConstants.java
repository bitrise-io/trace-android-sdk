package io.bitrise.trace.utils.log;

/**
 * String constants to be used for additional messaging around logging events to the Logger. 
 */
public final class LogMessageConstants {
    public static final String ACTIVITY_LIFECYCLE_COULD_NOT_BE_STARTED = "This means a TraceActivityLifecycleListener could not be created and Trace will not report any activity information for this session.";
    public static final String ACTIVITY_LIFECYCLE_TRACKER_INITIALISED = "Activity lifecycle tracker initialised.";
    public static final String APPLICATION_SESSION_MANAGER_INITIALISED = "Application session initialised.";
    public static final String APPLICATION_SESSION_MANAGER_STARTED = "Application session started.";
    public static final String APPLICATION_SESSION_MANAGER_STOPPED = "Application session stopped.";
    public static final String APPLICATION_TRACE_INITIALISED = "Application trace initialised.";
    public static final String APPLICATION_TRACE_STARTED = "Application trace started.";
    public static final String APPLICATION_TRACE_STOPPED = "Application trace stopped.";
    public static final String CONVERTER_FAILED_WITH_VALUE = "Converter failed to convert the following value: `%1$s`";
    public static final String COULD_NOT_REGISTER_FRAGMENT_LIFECYCLE_CALLBACK = "Trace may not report any information for this class.";
    public static final String COULD_NOT_DETERMINE_CDMA_OPERATOR_COUNTRY = "Could not determine CDMA operator country for this session, Trace may not report the device country for this session.";
    public static final String COULD_NOT_DETERMINE_HEADER_SIZE = "Could not determine header size. Trace may report this network stat incorrectly.";
    public static final String DATA_MANAGER_INITIALISED = "Data manager initialised.";
    public static final String DATA_MANAGER_START_COLLECTING = "Data manager started collecting.";
    public static final String DATA_MANAGER_START_SENDING = "Data manager started sending requests.";
    public static final String DATA_MANAGER_STOP_COLLECTING= "Data manager stopped collecting.";
    public static final String DATA_MANAGER_STOP_SENDING = "Data manager stopped sending requests.";
    public static final String DELETE_ALL_METRICS = "All metrics deleted from storage.";
    public static final String DELETE_ALL_RESOURCE_ENTITIES = "All resource entities deleted from storage.";
    public static final String DELETE_ALL_TRACES = "All traces deleted from storage.";
    public static final String DELETE_METRIC = "Metric deleted from storage.";
    public static final String DELETE_METRICS = "Metrics deleted from storage.";
    public static final String DELETE_RESOURCE_ENTITIES = "Resource entities deleted from storage.";
    public static final String DELETE_RESOURCE_WITH_SESSION_ID = "Resource resources with session id '%1$s' from " +
            "storage.";
    public static final String DELETE_TRACE = "Trace deleted from storage.";
    public static final String DELETE_TRACES = "Trace deleted from storage.";
    public static final String FAILED_TO_READ_APPLICATION_CPU_STATS = "Failed to read application CPU stats, Trace " +
            "may not report any CPU stats for this session.";
    public static final String FAILED_TO_READ_CPU_STATS = "Failed to read CPU stats, result is out of bounds. This " +
            "read will not be reported.";
    public static final String FAILED_TO_READ_SYSTEM_CPU_STATS = "Failed to read System CPU stats, Trace may not " +
            "report any CPU stats for this session.";
    public static final String INITIALISING_SDK = "Initialising the Trace SDK";
    public static final String METRIC_SENDING = "Attempting to send a metric.";
    public static final String METRIC_SENT_SUCCESSFULLY = "Metric sent successfully";
    public static final String SAVE_METRIC = "Metric saved to storage.";
    public static final String SAVE_RESOURCE = "Resource saved to storage.";
    public static final String SAVE_RESOURCE_ENTITY = "Resource entity saved to storage.";
    public static final String SAVE_TRACE = "Trace saved to storage.";
    public static final String SCHEDULER_SEND_CANCELLED = "Scheduler for sending data to Trace has been cancelled.";
    public static final String SCHEDULER_SEND_SCHEDULE_DELAYED = "Scheduler for sending data to Trace has been " +
            "created with a delay of '%1$s'.";
    public static final String SET_URL_STREAM_HANDLER_FACTORY_FAILED = "setURLStreamHandlerFactory failed. This means" +
            " the Trace SDK will not be able to capture network metrics that use URLConnection. Please refer to the " +
            "documentation for further guidance.";
    public static final String TRACE_SENDING = "Attempting to send a trace.";
    public static final String TRACE_SENT_SUCCESSFULLY = "Trace sent successfully";
    public static final String TRACE_SDK_SUCCESSFULLY_INITIALISED = "Trace SDK is successfully initialised.";
    public static final String URL_CONNECTION_REQUESTS_SUCCESS = "UrlConnection listening has successfully been configured, all requests using UrlConnection will be reported by the Trace SDK.";
}

