package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

import java.util.HashMap;
import java.util.Map;

import io.bitrise.trace.plugin.task.BaseTraceTask;

/**
 * Factory made for simplifying {@link BaseTraceTask} creation.
 */
public class TraceTaskBuilder {

    /**
     * The {@link TaskContainer} that will contain the newly created task.
     */
    @NonNull
    protected final TaskContainer taskContainer;

    /**
     * Tha base name of the task without any prefixes.
     */
    @NonNull
    protected final String baseName;
    /**
     * The type of the task.
     */
    @NonNull
    protected final Class<? extends BaseTraceTask> type;
    /**
     * The group for the task.
     */
    @Nullable
    protected String group;
    /**
     * The description for the task.
     */
    @Nullable
    protected String description;

    /**
     * Constructor for the class.
     *
     * @param taskContainer the {@link TaskContainer} that will contain the created {@link Task}.
     * @param baseName      the baseName of the newly created task.
     * @param type          the class of the task.
     */
    public TraceTaskBuilder(@NonNull final TaskContainer taskContainer,
                            @NonNull final String baseName,
                            @NonNull final Class<? extends BaseTraceTask> type) {
        this.taskContainer = taskContainer;
        this.baseName = baseName;
        this.type = type;
    }

    /**
     * Sets the group for the given task.
     *
     * @param group the group of the given task.
     * @return this builder.
     */
    @NonNull
    public TraceTaskBuilder setGroup(@Nullable final String group) {
        this.group = group;
        return this;
    }

    /**
     * Sets the description of the task.
     *
     * @param description the description of the task.
     * @return this builder
     */
    @NonNull
    public TraceTaskBuilder setDescription(@Nullable final String description) {
        this.description = description;
        return this;
    }

    /**
     * Creates the properties for a Gradle task
     *
     * @param name the name of the Task.
     * @param type the class of the Task.
     * @return a property Map that is required for task creation.
     */
    @NonNull
    protected Map<String, Object> generatePropertiesForTask(@NonNull final String name, @NonNull final Class<?
            extends BaseTraceTask> type) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("type", type);
        if (group != null) {
            properties.put("group", group);
        }
        if (description != null) {
            properties.put("description", description);
        }
        return properties;
    }

    /**
     * Builds the given task based on the values that are set and adds it to the {@link TaskContainer}.
     *
     * @return the newly created task.
     */
    @NonNull
    public BaseTraceTask build() {
        return (BaseTraceTask) taskContainer.create(generatePropertiesForTask(baseName, type));
    }
}
