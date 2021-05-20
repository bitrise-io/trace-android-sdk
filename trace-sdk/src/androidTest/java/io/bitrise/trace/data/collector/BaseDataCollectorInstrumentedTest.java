package io.bitrise.trace.data.collector;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TestDataStorage;
import org.junit.BeforeClass;

/**
 * Base class for instrumented tests for {@link DataCollector}s. Needed for {@link DataCollector}s,
 * whose {@link Data} is pushed directly to the {@link DataStorage} through the
 * {@link DataManager}. The {@link #setUpBeforeClass()} method ensures that the
 * {@link DataManager} will use a {@link TestDataStorage} instead of a DataStorage, because
 * instrumented tests will not create the Database file, and writing to it would lead to exception.
 */
public abstract class BaseDataCollectorInstrumentedTest {

  /**
   * The Android Context.
   */
  @NonNull
  protected static Context context;

  /**
   * Sets up the initial state for the test class. Note: if a test already implemented a
   * BeforeClass method, it cannot extend this, as it will make the test fail. Calls
   * {@link #useTestDataStorage(Context)} to ensure we use a {@link TestDataStorage} for the tests.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    context = ApplicationProvider.getApplicationContext();
    useTestDataStorage(context);
  }

  /**
   * Ensures that the {@link DataManager} will use a {@link TestDataStorage} instead of a
   * {@link DataStorage}, because instrumented tests will not create the Database file, and
   * writing to it would lead to exception.
   *
   * @param context the Android Context.
   */
  public static void useTestDataStorage(@NonNull final Context context) {
    final DataManager dataManager = DataManager.getInstance(context);
    dataManager.setDataStorage(TestDataStorage.getInstance(context));
  }
}
