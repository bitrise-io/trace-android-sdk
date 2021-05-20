package io.bitrise.trace.data.collector.device;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.bitrise.trace.data.collector.DataCollector;
import io.bitrise.trace.data.dto.Data;
import java.util.UUID;

/**
 * {@link DataCollector} type, that collects the ID of the device.
 *
 * <p>Note: this device ID is a GUID, not the real device ID, so with reinstall or with clearing the
 * stored data it could change. However this is still the recommended approach.
 *
 * @see
 * <a href="https://developer.android.com/training/articles/user-data-ids">https://developer.android.com/training/articles/user-data-ids</a>
 */
public class DeviceIdDataCollector extends DeviceDataCollector {

  /**
   * The key name of the device ID value stored in the SharedPreferences.
   */
  private static final String DEVICE_ID_KEY = "DeviceIdKey";
  /**
   * The file name of the SharedPreferences file, that holds the ID for the device.
   */
  private static final String SHARED_PREFERENCES_FILE_NAME = "trace.id";

  @NonNull
  private final Context context;

  /**
   * Constructor for class.
   *
   * @param context the Android Context.
   */
  public DeviceIdDataCollector(@NonNull final Context context) {
    this.context = context;
  }

  @NonNull
  @Override
  public Data collectData() {
    final Data data = new Data(this);
    data.setContent(getOrCreateDeviceId(context));
    return data;
  }

  @NonNull
  @Override
  public String[] getPermissions() {
    return new String[0];
  }

  /**
   * Gets the ID for the device, or creates it if not present. The ID is unique for the given
   * device.
   *
   * @param context the Android Context.
   * @return the ID for the device.
   */
  @NonNull
  private synchronized String getOrCreateDeviceId(@NonNull final Context context) {
    final String savedDeviceId = getDeviceId(context);
    if (savedDeviceId == null) {
      return createDeviceId(context);
    }
    return savedDeviceId;
  }

  /**
   * Gets the ID for the device.
   *
   * @param context the Android Context.
   * @return the ID for the device, or {@code null}, when the ID cannot be found in the
   *     SharedPreferences.
   */
  @Nullable
  private String getDeviceId(@NonNull final Context context) {
    final SharedPreferences sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE);
    return sharedPreferences.getString(DEVICE_ID_KEY, null);
  }

  /**
   * Creates a unique ID for the device.
   *
   * @param context the Android Context.
   * @return the created ID.
   */
  @NonNull
  private String createDeviceId(@NonNull final Context context) {
    final String deviceId = UUID.randomUUID().toString();

    final SharedPreferences sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE);
    final SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(DEVICE_ID_KEY, deviceId);
    editor.apply();
    return deviceId;
  }
}
