package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * Base abstract class for the tasks of the Trace plugin.
 */
public abstract class BaseTraceTask extends DefaultTask {

  /**
   * The {@link Project} which can use this task.
   */
  @NonNull
  protected Project project = getProject();

  /**
   * The {@link Logger} for logging.
   */
  @NonNull
  protected Logger logger = project.getLogger();
}