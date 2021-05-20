package io.bitrise.trace.data.storage;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.test.DataTestUtils;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Contains tests for the {@link ResourceDao}.
 */
public class ResourceDaoInstrumentedTest {

  private static ResourceDao resourceDao;
  private static TraceDatabase traceDatabase;

  /**
   * Sets up the initial state for the test class.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    final Context context = ApplicationProvider.getApplicationContext();
    traceDatabase = Room.inMemoryDatabaseBuilder(context, TraceDatabase.class).build();
    resourceDao = traceDatabase.getResourceDao();
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
   * Asserts that if we add a {@link ResourceEntity} to the {@link TraceDatabase} via the
   * {@link MetricDao} it will be returned when we query it.
   */
  @Test
  public void insertAll_shouldContainInsertedValue() {
    final ResourceEntity expectedValue = DataTestUtils.getSampleResourceEntity();
    resourceDao.insertAll(expectedValue);
    final List<ResourceEntity> actualValues = resourceDao.getAll();
    assertThat(actualValues, hasItem(expectedValue));
  }

  /**
   * Asserts that if we add a {@link ResourceEntity} to the {@link TraceDatabase} via the
   * {@link ResourceDao}, then delete it, it will not be returned when queried.
   */
  @Test
  public void deleteById_shouldDeleteValue() {
    final ResourceEntity resourceEntity = DataTestUtils.getSampleResourceEntity();
    resourceDao.insertAll(resourceEntity);
    resourceDao.deleteById(resourceEntity.getId());
    final List<ResourceEntity> actualValues = resourceDao.getAll();
    assertThat(actualValues, not(hasItem(resourceEntity)));
  }

  /**
   * Asserts that if we add a {@link ResourceEntity} to the {@link TraceDatabase} via the
   * {@link ResourceDao} multiple times, only one instance will be stored.
   */
  @Test
  public void update_shouldBeOneInstance() {
    final ResourceEntity resourceEntity = DataTestUtils.getSampleResourceEntity();
    resourceDao.deleteAll();
    resourceDao.insertAll(resourceEntity);
    resourceDao.insertAll(resourceEntity);
    final List<ResourceEntity> actualValues = resourceDao.getAll();
    assertThat(actualValues.size(), is(1));
  }

  /**
   * Asserts that if we add {@link ResourceEntity}s to the {@link TraceDatabase}, then we query
   * them, all of them will be returned.
   */
  @Test
  public void getAll_shouldReturnAll() {
    final ResourceEntity sampleResourceEntity = DataTestUtils.getSampleResourceEntity();
    final ResourceEntity otherResourceEntity = DataTestUtils.getOtherResourceEntity();
    resourceDao.insertAll(sampleResourceEntity, otherResourceEntity);

    final List<ResourceEntity> actualValues = resourceDao.getAll();
    final ResourceEntity[] expectedValues =
        new ResourceEntity[] {sampleResourceEntity, otherResourceEntity};
    assertThat(actualValues, containsInAnyOrder(expectedValues));
  }

  /**
   * Asserts that if we add {@link ResourceEntity}s to the {@link TraceDatabase}, then we query
   * them, all of them will be returned.
   */
  @Test
  public void getAllWithSessionId_shouldReturnAllWithSessionId() {
    final String sessionId = "SomeSessionId";
    final ResourceEntity sampleResourceEntity = DataTestUtils.getSampleResourceEntity();
    sampleResourceEntity.setSessionId(sessionId);
    final ResourceEntity otherResourceEntity = DataTestUtils.getOtherResourceEntity();
    otherResourceEntity.setSessionId(sessionId);
    final ResourceEntity differentResourceEntity = DataTestUtils.getDifferentResourceEntity();
    differentResourceEntity.setSessionId("DifferentSessionId");
    resourceDao.insertAll(sampleResourceEntity, otherResourceEntity, differentResourceEntity);

    final List<ResourceEntity> actualValues = resourceDao.getAllWithSessionId(sessionId);
    final ResourceEntity[] expectedValues =
        new ResourceEntity[] {sampleResourceEntity, otherResourceEntity};
    assertThat(actualValues, containsInAnyOrder(expectedValues));
  }

  /**
   * Asserts that if we add {@link ResourceEntity}s to the {@link TraceDatabase}, then we delete
   * all, the database will be empty.
   */
  @Test
  public void deleteAll_shouldMakeEmpty() {
    final ResourceEntity sampleResourceEntity = DataTestUtils.getSampleResourceEntity();
    final ResourceEntity otherResourceEntity = DataTestUtils.getOtherResourceEntity();

    resourceDao.insertAll(sampleResourceEntity, otherResourceEntity);
    resourceDao.deleteAll();
    final int actualValue = resourceDao.getAll().size();
    assertThat(actualValue, is(0));
  }

  /**
   * Asserts that if we add {@link ResourceEntity}s to the {@link TraceDatabase}, then we
   * delete by {@link Session} ID, only ResourceEntities with different Session ID will be left
   * in the database.
   */
  @Test
  public void deleteBySessionId_shouldDeleteOnlyWithId() {
    final String sessionId = "SomeSessionId";
    final ResourceEntity sampleResourceEntity = DataTestUtils.getSampleResourceEntity();
    sampleResourceEntity.setSessionId(sessionId);
    final ResourceEntity otherResourceEntity = DataTestUtils.getOtherResourceEntity();
    otherResourceEntity.setSessionId(sessionId);
    final ResourceEntity differentResourceEntity = DataTestUtils.getDifferentResourceEntity();
    differentResourceEntity.setSessionId("DifferentSessionId");
    resourceDao.insertAll(sampleResourceEntity, otherResourceEntity, differentResourceEntity);

    resourceDao.deleteBySessionId(sessionId);
    final List<ResourceEntity> actualValues = resourceDao.getAll();
    final ResourceEntity[] expectedValues = new ResourceEntity[] {differentResourceEntity};
    assertThat(actualValues, containsInAnyOrder(expectedValues));
  }
}
