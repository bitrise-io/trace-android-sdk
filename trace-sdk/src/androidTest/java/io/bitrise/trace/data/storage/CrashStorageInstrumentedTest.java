package io.bitrise.trace.data.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.data.storage.entities.CrashEntity;
import io.bitrise.trace.network.CrashRequest;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.DataTestUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Instrumentation tests for saving {@link CrashEntity} into {@link DataStorage}.
 */
public class CrashStorageInstrumentedTest {

  private static DataStorage dataStorage;

  @BeforeClass
  public static void setUp() {
    final Context context = ApplicationProvider.getApplicationContext();
    dataStorage = TraceDataStorage.getInstance(context);
    dataStorage.traceDatabase = Room.inMemoryDatabaseBuilder(context, TraceDatabase.class).build();
  }

  @AfterClass
  public static void tearDown() {
    ApplicationSessionManager.getInstance().stopSession();
    dataStorage.traceDatabase.close();
  }

  @Before
  public void setUpForEach() {
    dataStorage.traceDatabase.clearAllTables();
  }

  private CrashRequest createCrashRequest(@NonNull final String uuid,
                                           final long timestamp) {
    return new CrashRequest(
        DataTestUtils.getSampleResource("session-id"),
        createCrashReport(),
        timestamp,
        uuid,
        "trace-id",
        "span-id"
    );
  }

  public static CrashReport createCrashReport() {
    final List<CrashReport.Thread> threads = new ArrayList<>();
    threads.add(new CrashReport.Thread(1L, true, createStackTraceFrames()));
    threads.add(new CrashReport.Thread(12345L, false, createStackTraceFrames()));

    return new CrashReport(threads, "throwable class name", "description",
        null);
  }
  static List<CrashReport.Frame> createStackTraceFrames() {
    final List<CrashReport.Frame> frames = new ArrayList<>();
    frames.add(new CrashReport.Frame("class1", "method1", "file1", 1, 0));
    frames.add(new CrashReport.Frame("class2", "method2", "file2", 2, 1));
    frames.add(new CrashReport.Frame("class3", "method3", "file3", 3, 2));
    return frames;
  }

  @Test
  public void saveOneCrashReportShouldContainOneRecord() {
    final CrashRequest request1 = createCrashRequest("uuid1", 1628778619944L);

    dataStorage.saveCrashRequest(request1);

    final List<CrashRequest> requests = dataStorage.getAllCrashRequests();

    assertEquals(1, requests.size());
    assertEquals(request1, requests.get(0));
  }

  @Test
  public void saveTwoCrashReportShouldContainTwoRecord() {
    final CrashRequest request1 = createCrashRequest("uuid1", 1628778619944L);
    final CrashRequest request2 = createCrashRequest("uuid2", 1628778629944L);

    dataStorage.saveCrashRequest(request1);
    dataStorage.saveCrashRequest(request2);

    final List<CrashRequest> requests = dataStorage.getAllCrashRequests();
    assertEquals(2, requests.size());

    assertTrue(requests.contains(request1));
    assertTrue(requests.contains(request2));

  }

  @Test
  public void deleteOneRecord() {
    final CrashRequest request1 = createCrashRequest("uuid1", 1628778619944L);
    final CrashRequest request2 = createCrashRequest("uuid2", 1628778629944L);

    dataStorage.saveCrashRequest(request1);
    dataStorage.saveCrashRequest(request2);

    final List<CrashRequest> requests = dataStorage.getAllCrashRequests();
    assertEquals(2, requests.size());

    dataStorage.deleteCrashRequest("uuid1");

    final List<CrashRequest> updatedRequests = dataStorage.getAllCrashRequests();
    assertEquals(1, updatedRequests.size());

    assertTrue(updatedRequests.contains(request2));
  }


}
