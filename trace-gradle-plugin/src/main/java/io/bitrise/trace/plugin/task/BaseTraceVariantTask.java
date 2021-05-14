package io.bitrise.trace.plugin.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

/**
 * Base abstract class for the tasks of the Trace plugin. A TraceVariantTask is built for each
 * variant and for each variant output.
 */
public abstract class BaseTraceVariantTask extends BaseTraceTask {

  /**
   * The variant for the given task.
   */
  @NonNull
  @Input
  private BaseVariant variant;

  /**
   * The variant output for the given task.
   */
  @Nullable
  @Optional
  @Input
  private BaseVariantOutput variantOutput;

  /**
   * Gets the variant.
   *
   * @return the variant.
   */
  @NonNull
  public BaseVariant getVariant() {
    return variant;
  }

  /**
   * Sets the variant to the given value.
   *
   * @param variant the variant.
   */
  public void setVariant(@NonNull final BaseVariant variant) {
    this.variant = variant;
  }

  /**
   * Gets the variant output.
   *
   * @return the variant output.
   */
  @Nullable
  public BaseVariantOutput getVariantOutput() {
    return variantOutput;
  }

  /**
   * Sets the variant output to the given value.
   *
   * @param variantOutput the variant output.
   */
  public void setVariantOutput(@Nullable final BaseVariantOutput variantOutput) {
    this.variantOutput = variantOutput;
  }
}
