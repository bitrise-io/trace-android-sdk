package io.bitrise.trace.data.collector.memory;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;

/**
 * {@link DataCollector} type, that collects total amount of used memory by the device. Memory
 * management is a complex topic, to keep it simple, we calculate it as the Process Stats do it.
 * It is based on the PSS (Proportional Set Size). For more details see the linked pages.
 *
 * @see
 * <a href="https://android-developers.googleblog.com/2014/01/process-stats-understanding-how-your.html">https://android-developers.googleblog.com/2014/01/process-stats-understanding-how-your.html</a>
 * @see
 * <a href="https://developer.android.com/studio/profile/memory-profiler">https://developer.android.com/studio/profile/memory-profiler</a>
 */
public class SystemMemoryDataCollector extends MemoryDataCollector {

  @NonNull
  private final Context context;

  /**
   * Constructor for class.
   *
   * @param context the Android Context.
   */
  public SystemMemoryDataCollector(@NonNull final Context context) {
    this.context = context;
  }

  @NonNull
  @Override
  public Data collectData() {
    final Data data = new Data(this);
    data.setContent(getTotalUsedMemory());
    return data;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }

  /**
   * Gets the total amount of used memory for the device.
   *
   * @return the long value of the memory amount, or {@code null}, when we cannot access to the
   *     {@link Context#ACTIVITY_SERVICE}.
   */
  @Nullable
  private Long getTotalUsedMemory() {
    final ActivityManager activityManager =
        (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
    if (activityManager == null) {
      return null;
    }

    final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
    activityManager.getMemoryInfo(memoryInfo);
    return memoryInfo.totalMem - memoryInfo.availMem;
  }
}
