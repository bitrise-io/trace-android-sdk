package io.bitrise.trace.data.storage;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.data.trace.TraceEntity;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.test.TraceTestProvider;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Contains tests for the {@link TraceDao}.
 */
public class TraceDaoInstrumentedTest {

  private static TraceDao traceDao;
  private static TraceDatabase traceDatabase;

  /**
   * Sets up the initial state for the test class.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    final Context context = ApplicationProvider.getApplicationContext();
    traceDatabase = Room.inMemoryDatabaseBuilder(context, TraceDatabase.class).build();
    traceDao = traceDatabase.getTraceDao();
    ApplicationSessionManager.getInstance().startSession();
  }

  /**
   * Tears down the required objects after all the tests run.
   */
  @AfterClass
  public static void tearDownClass() {
    ApplicationSessionManager.getInstance().stopSession();
    traceDatabase.close();
  }


  /**
   * Sets up the initial state for each test case.
   */
  @Before
  public void setUp() {
    traceDao.deleteAll();
  }

  /**
   * Asserts that if we add a {@link TraceEntity} to the {@link TraceDatabase} via the
   * {@link TraceDao} it will be returned when we query it.
   */
  @Test
  public void insertAll_shouldContainInsertedValue() {
    final Trace expectedValue = TraceTestProvider.getEmptyTrace();
    traceDao.insertAll(new TraceEntity(expectedValue));
    final Trace actualValue = traceDao.getById(expectedValue.getTraceId()).getTrace();
    assertThat(actualValue, equalTo(expectedValue));
  }

  /**
   * Asserts that if we add a {@link TraceEntity} to the {@link TraceDatabase} via the
   * {@link TraceDao}, then delete it, it will not be returned when queried.
   */
  @Test
  public void deleteById_shouldDeleteValue() {
    final TraceEntity traceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    traceDao.insertAll(traceEntity);
    traceDao.deleteById(traceEntity.getTraceId());
    final List<TraceEntity> actualValues = traceDao.getAll();
    assertThat(actualValues, not(hasItem(traceEntity)));
  }

  /**
   * Asserts that if we add a {@link TraceEntity} to the {@link TraceDatabase} via the
   * {@link TraceDao} multiple times, only one instance will be stored.
   */
  @Test
  public void update_shouldBeOneInstance() {
    final TraceEntity traceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    traceDao.deleteAll();
    traceDao.insertAll(traceEntity);
    traceDao.insertAll(traceEntity);
    final List<TraceEntity> actualValues = traceDao.getAll();
    assertThat(actualValues.size(), is(1));
  }

  /**
   * Asserts that if we add {@link TraceEntity}s to the {@link TraceDatabase} with multiple
   * {@link Session} IDs, then we query the Session IDs, all of them will be returned.
   */
  @Test
  public void getSessionIds_shouldReturnAllSessions() {
    final TraceEntity sampleTraceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    sampleTraceEntity.setSessionId("FirstSession");
    final TraceEntity otherTraceEntity = new TraceEntity(TraceTestProvider.getOtherTrace());
    otherTraceEntity.setSessionId("SecondSession");
    traceDao.insertAll(sampleTraceEntity, otherTraceEntity);

    final List<String> actualValues = traceDao.getSessionIds();
    final String[] expectedValues = new String[] {"FirstSession", "SecondSession"};
    assertThat(actualValues, containsInAnyOrder(expectedValues));
  }

  /**
   * Asserts that if we add {@link TraceEntity}s to the {@link TraceDatabase}, then we query
   * them, all of them will be returned.
   */
  @Test
  public void getAll_shouldReturnAll() {
    final TraceEntity sampleTraceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    final TraceEntity otherTraceEntity = new TraceEntity(TraceTestProvider.getOtherTrace());
    traceDao.insertAll(sampleTraceEntity, otherTraceEntity);

    final List<TraceEntity> actualValues = traceDao.getAll();
    final TraceEntity[] expectedValues = new TraceEntity[] {sampleTraceEntity, otherTraceEntity};
    assertThat(actualValues, containsInAnyOrder(expectedValues));
  }

  /**
   * Asserts that if we add {@link TraceEntity}s to the {@link TraceDatabase}, then we query
   * them, all of them will be returned.
   */
  @Test
  public void getBySessionId_shouldReturnAllWithSessionId() {
    final String sessionId = "SomeSessionId";
    final TraceEntity sampleTraceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    sampleTraceEntity.setSessionId(sessionId);
    final TraceEntity otherTraceEntity = new TraceEntity(TraceTestProvider.getOtherTrace());
    otherTraceEntity.setSessionId(sessionId);
    final TraceEntity differentTraceEntity = new TraceEntity(TraceTestProvider.getEmptyTrace());
    differentTraceEntity.setSessionId("DifferentSessionId");
    traceDao.insertAll(sampleTraceEntity, otherTraceEntity, differentTraceEntity);

    final List<TraceEntity> actualValues = traceDao.getBySessionId(sessionId);
    final TraceEntity[] expectedValues = new TraceEntity[] {sampleTraceEntity, otherTraceEntity};
    assertThat(actualValues, containsInAnyOrder(expectedValues));
  }

  /**
   * Asserts that if we add {@link TraceEntity}s to the {@link TraceDatabase}, then we query one
   * of them its ID, it will return the one we added.
   */
  @Test
  public void getById_shouldReturnTheSame() {
    final TraceEntity sampleTraceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    final TraceEntity otherTraceEntity = new TraceEntity(TraceTestProvider.getOtherTrace());

    traceDao.insertAll(sampleTraceEntity, otherTraceEntity);

    final TraceEntity actualValue = traceDao.getById(sampleTraceEntity.getTraceId());
    assertThat(actualValue, is(sampleTraceEntity));
  }

  /**
   * Asserts that if we add {@link TraceEntity}s to the {@link TraceDatabase}, then we query a
   * non existing ID, it will return {@code null}.
   */
  @Test
  public void getById_shouldReturnNull() {
    final TraceEntity sampleTraceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    final TraceEntity otherTraceEntity = new TraceEntity(TraceTestProvider.getOtherTrace());

    traceDao.insertAll(sampleTraceEntity, otherTraceEntity);

    final TraceEntity actualValue = traceDao.getById(
        sampleTraceEntity.getTraceId() + otherTraceEntity.getTraceId());
    assertThat(actualValue, is(nullValue()));
  }

  /**
   * Asserts that if we add {@link TraceEntity}s to the {@link TraceDatabase}, then we delete
   * all, the database will be empty.
   */
  @Test
  public void deleteAll_shouldMakeEmpty() {
    final TraceEntity sampleTraceEntity = new TraceEntity(TraceTestProvider.getSampleTrace());
    final TraceEntity otherTraceEntity = new TraceEntity(TraceTestProvider.getOtherTrace());

    traceDao.insertAll(sampleTraceEntity, otherTraceEntity);
    traceDao.deleteAll();
    final int actualValue = traceDao.getAll().size();
    assertThat(actualValue, is(0));
  }
}
