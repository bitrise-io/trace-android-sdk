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
import java.util.TimeZone;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Instrumentation tests for saving {@link CrashEntity} into {@link DataStorage}.
 */
public class CrashStorageInstrumentedTest {

  private static DataStorage dataStorage;

  /**
   * Creates an empty data storage.
   */
  @BeforeClass
  public static void setUp() {
    final Context context = ApplicationProvider.getApplicationContext();
    dataStorage = TraceDataStorage.getInstance(context);
    dataStorage.traceDatabase = Room.inMemoryDatabaseBuilder(context, TraceDatabase.class).build();
  }

  /**
   * Ensures the data storage is cleared.
   */
  @AfterClass
  public static void tearDown() {
    ApplicationSessionManager.getInstance().stopSession();
    dataStorage.traceDatabase.close();
  }

  @Before
  public void setUpForEach() {
    dataStorage.traceDatabase.clearAllTables();
  }

  /**
   * Creates a dummy {@link CrashRequest} object.
   *
   * @param uuid a unique identifier for the crash report.
   * @param timestamp a timestamp in milliseconds for the crash report.
   *
   * @return a crash request object for testing purposes.
   */
  private CrashRequest createCrashRequest(@NonNull final String uuid,
                                           final long timestamp) {
    return new CrashRequest(
        DataTestUtils.getSampleResource("session-id"),
        createCrashReport(),
        timestamp,
        TimeZone.getDefault(),
        uuid,
        "trace-id",
        "span-id"
    );
  }

  /**
   * Creates a dummy{@link CrashReport}.
   *
   * @return a crash report for testing purposes.
   */
  static CrashReport createCrashReport() {
    final List<CrashReport.Thread> threads = new ArrayList<>();
    threads.add(new CrashReport.Thread(1L, true, createStackTraceFrames()));
    threads.add(new CrashReport.Thread(12345L, false, createStackTraceFrames()));

    return new CrashReport(threads, "throwable class name", "description",
        null);
  }

  /**
   * Creates a list of dummy {@link CrashReport.Frame}'s.
   *
   * @return some stack trace frames for testing purposes.
   */
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

  @Test
  public void updateCrashRequest() {
    // given a crash request in the data store
    final CrashRequest request1 = createCrashRequest("uuid1", 1628778619944L);
    dataStorage.saveCrashRequest(request1);

    // verify there is only one record
    assertEquals(1, dataStorage.getAllCrashRequests().size());

    // update the counter
    dataStorage.updateCrashRequestSentAttemptCounter(request1.getMetadata().getUuid());

    // verify there is still one record
    assertEquals(1, dataStorage.getAllCrashRequests().size());

    // verify our crash entity increased attempts
    final CrashEntity crashEntity = dataStorage.getCrashEntity(request1.getMetadata().getUuid());
    assertEquals(1, crashEntity.getSentAttempts());
  }

  @Test
  public void updateCrashRequest_idDoesNotExist() {
    // given a crash request in the data store
    final CrashRequest request1 = createCrashRequest("uuid1", 1628778619944L);
    dataStorage.saveCrashRequest(request1);

    // verify there is only one record
    assertEquals(1, dataStorage.getAllCrashRequests().size());

    // update the counter for a crash that doesn't exist
    dataStorage.updateCrashRequestSentAttemptCounter("uuid2");

    // verify there is still one record
    assertEquals(1, dataStorage.getAllCrashRequests().size());
  }

  @Test
  public void updateCrashRequestMaxAttempts() {
    // given a crash request in the data store
    final CrashRequest request = createCrashRequest("uuid1", 1628778619944L);
    dataStorage.saveCrashRequest(request);

    // verify there is only one record
    assertEquals(1, dataStorage.getAllCrashRequests().size());

    // update the counter 4 times
    dataStorage.updateCrashRequestSentAttemptCounter(request.getMetadata().getUuid());
    dataStorage.updateCrashRequestSentAttemptCounter(request.getMetadata().getUuid());
    dataStorage.updateCrashRequestSentAttemptCounter(request.getMetadata().getUuid());
    dataStorage.updateCrashRequestSentAttemptCounter(request.getMetadata().getUuid());

    // verify there are still records
    assertEquals(1, dataStorage.getAllCrashRequests().size());

    // update one final time
    dataStorage.updateCrashRequestSentAttemptCounter(request.getMetadata().getUuid());

    // verify there are no records left
    assertEquals(0, dataStorage.getAllCrashRequests().size());
  }

}
