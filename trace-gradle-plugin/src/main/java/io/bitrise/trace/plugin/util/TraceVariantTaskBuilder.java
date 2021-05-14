package io.bitrise.trace.plugin.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import io.bitrise.trace.plugin.task.BaseTraceVariantTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

/**
 * Factory made for simplifying {@link BaseTraceVariantTask} creation.
 */
public class TraceVariantTaskBuilder extends TraceTaskBuilder {

  /**
   * The type of the task.
   */
  @NonNull
  private final Class<? extends BaseTraceVariantTask> type;

  /**
   * The {@link BaseVariant} for the task.
   */
  @NonNull
  private final BaseVariant variant;

  /**
   * The {@link BaseVariantOutput} for the task.
   */
  @Nullable
  private BaseVariantOutput variantOutput;

  /**
   * Constructor for the class.
   *
   * @param taskContainer the {@link TaskContainer} that will contain the created {@link Task}.
   * @param baseName      the baseName of the newly created task.
   * @param type          the class of the task.
   * @param variant       the {@link BaseVariant} for the task.
   */
  public TraceVariantTaskBuilder(@NonNull final TaskContainer taskContainer,
                                 @NonNull final String baseName,
                                 @NonNull final Class<? extends BaseTraceVariantTask> type,
                                 @NonNull final BaseVariant variant) {
    super(taskContainer, baseName, type);
    this.type = type;
    this.variant = variant;
  }

  /**
   * Sets the {@link BaseVariantOutput} for the given task.
   *
   * @param variantOutput the variant output.
   * @return this builder.
   */
  @NonNull
  public TraceVariantTaskBuilder setVariantOutput(
      @Nullable final BaseVariantOutput variantOutput) {
    this.variantOutput = variantOutput;
    return this;
  }

  @Override
  @NonNull
  public BaseTraceVariantTask build() {
    final BaseTraceVariantTask baseTraceVariantTask =
        (BaseTraceVariantTask) taskContainer
            .create(generatePropertiesForTask(generateTaskName(), type));
    baseTraceVariantTask.setVariant(variant);
    baseTraceVariantTask.setVariantOutput(variantOutput);
    return baseTraceVariantTask;
  }

  /**
   * Generates the task name from the {@link #baseName}, {@link #variant} and
   * {@link #variantOutput}. The order will be: <i>VariantOutputName+VariantName+BaseName</i>. It
   * uses {@link FormattingStringBuilder} for the formatting. Note that variant name will be
   * omitted when it is the same with the variant output.
   *
   * <p>Example: When the base name = 'myTask', variant name = 'Variant', variant output name =
   * 'variant', the result will be: <b>'variantMyTask'</b>.
   *
   * @return the formatted task name.
   */
  @NonNull
  private String generateTaskName() {
    final FormattingStringBuilder formattingStringBuilder = new FormattingStringBuilder();
    String variantOutputName = null;
    if (variantOutput != null) {
      variantOutputName = variantOutput.getName();
      formattingStringBuilder.append(variantOutputName);
    }
    final String variantName = variant.getName();
    if (!variantName.equalsIgnoreCase(variantOutputName)) {
      formattingStringBuilder.append(variantName);
    }

    formattingStringBuilder.appendWithCapitalizeFirst(baseName);
    return formattingStringBuilder.toString();
  }
}
