package io.bitrise.trace.data.storage;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.Metric;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Contains tests for the {@link MetricDao}.
 */
public class MetricDaoInstrumentedTest {

    private static MetricDao metricDao;
    private static TraceDatabase traceDatabase;

    /**
     * Sets up the initial state for the test class.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        final Context context = ApplicationProvider.getApplicationContext();
        traceDatabase = Room.inMemoryDatabaseBuilder(context, TraceDatabase.class).build();
        metricDao = traceDatabase.getMetricDao();
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
        metricDao.deleteAll();
    }

    /**
     * Asserts that if we add a {@link MetricEntity} to the {@link TraceDatabase} via the {@link MetricDao} it will be
     * returned when we query it.
     */
    @Test
    public void insertAll_shouldContainInsertedValue() {
        final MetricEntity expectedValue = new MetricEntity(MetricTestProvider.getEmptyMetric());
        metricDao.insertAll(expectedValue);
        final List<MetricEntity> actualValues = metricDao.getAll();
        assertThat(actualValues, hasItem(expectedValue));
    }

    /**
     * Asserts that if we add a {@link MetricEntity} with some data to the {@link TraceDatabase}
     * via the {@link MetricDao} it will be returned when we query it.
     */
    @Test
    public void insertAll_realData() {
        final Metric expectedValue = MetricTestProvider.getApplicationStartUpMetric();
        metricDao.insertAll(new MetricEntity(expectedValue));
        final List<MetricEntity> actualValues = metricDao.getAll();
        assertEquals(expectedValue, actualValues.get(0).getMetric());
    }

    /**
     * Asserts that if we add a {@link MetricEntity} to the {@link TraceDatabase} via the {@link MetricDao}, then delete
     * it, it will not be returned when queried.
     */
    @Test
    public void deleteById_shouldDeleteValue() {
        final Metric metric = MetricTestProvider.getSampleMetric();
        final MetricEntity metricEntity = new MetricEntity(metric);
        metricDao.insertAll(metricEntity);
        metricDao.deleteById(metricEntity.getMetricId());
        final List<MetricEntity> actualValues = metricDao.getAll();
        assertThat(actualValues, not(hasItem(metricEntity)));
    }

    /**
     * Asserts that if we add a {@link MetricEntity} to the {@link TraceDatabase} via the {@link MetricDao} multiple
     * times, only one instance will be stored.
     */
    @Test
    public void update_shouldBeOneInstance() {
        final MetricEntity metricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
        metricDao.deleteAll();
        metricDao.insertAll(metricEntity);
        metricDao.insertAll(metricEntity);
        final List<MetricEntity> actualValues = metricDao.getAll();
        assertThat(actualValues.size(), is(1));
    }

    /**
     * Asserts that if we add {@link MetricEntity}s to the {@link TraceDatabase} with multiple {@link Session} IDs,
     * then we query the Session IDs, all of them will be returned.
     */
    @Test
    public void getSessionIds_shouldReturnAllSessions() {
        final MetricEntity sampleMetricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
        sampleMetricEntity.setSessionId("FirstSession");
        final MetricEntity otherMetricEntity = new MetricEntity(MetricTestProvider.getOtherMetric());
        otherMetricEntity.setSessionId("SecondSession");
        metricDao.insertAll(sampleMetricEntity, otherMetricEntity);

        final List<String> actualValues = metricDao.getSessionIds();
        final String[] expectedValues = new String[]{"FirstSession", "SecondSession"};
        assertThat(actualValues, containsInAnyOrder(expectedValues));
    }

    /**
     * Asserts that if we add {@link MetricEntity}s to the {@link TraceDatabase}, then we query them, all of them
     * will be returned.
     */
    @Test
    public void getAll_shouldReturnAll() {
        final MetricEntity sampleMetricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
        final MetricEntity otherMetricEntity = new MetricEntity(MetricTestProvider.getOtherMetric());
        metricDao.insertAll(sampleMetricEntity, otherMetricEntity);

        final List<MetricEntity> actualValues = metricDao.getAll();
        final MetricEntity[] expectedValues = new MetricEntity[]{sampleMetricEntity, otherMetricEntity};
        assertThat(actualValues, containsInAnyOrder(expectedValues));
    }

    /**
     * Asserts that if we add {@link MetricEntity}s to the {@link TraceDatabase}, then we query them, all of them
     * will be returned.
     */
    @Test
    public void getBySessionId_shouldReturnAllWithSessionId() {
        final String sessionID = "SomeSessionId";
        final MetricEntity sampleMetricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
        sampleMetricEntity.setSessionId(sessionID);
        final MetricEntity otherMetricEntity = new MetricEntity(MetricTestProvider.getOtherMetric());
        otherMetricEntity.setSessionId(sessionID);
        final MetricEntity differentMetricEntity = new MetricEntity(MetricTestProvider.getEmptyMetric());
        differentMetricEntity.setSessionId("DifferentSessionId");
        metricDao.insertAll(sampleMetricEntity, otherMetricEntity, differentMetricEntity);

        final List<MetricEntity> actualValues = metricDao.getBySessionId(sessionID);
        final MetricEntity[] expectedValues = new MetricEntity[]{sampleMetricEntity, otherMetricEntity};
        assertThat(actualValues, containsInAnyOrder(expectedValues));
    }

    /**
     * Asserts that if we add {@link MetricEntity}s to the {@link TraceDatabase}, then we query one of them its ID,
     * it will return the one we added.
     */
    @Test
    public void getById_shouldReturnTheSame() {
        final MetricEntity sampleMetricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
        final MetricEntity otherMetricEntity = new MetricEntity(MetricTestProvider.getOtherMetric());

        metricDao.insertAll(sampleMetricEntity, otherMetricEntity);

        final MetricEntity actualValue = metricDao.getById(sampleMetricEntity.getMetricId());
        assertThat(actualValue, is(sampleMetricEntity));
    }

    /**
     * Asserts that if we add {@link MetricEntity}s to the {@link TraceDatabase}, then we query a non existing ID, it
     * will return {@code null}.
     */
    @Test
    public void getById_shouldReturnNull() {
        final MetricEntity sampleMetricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
        final MetricEntity otherMetricEntity = new MetricEntity(MetricTestProvider.getOtherMetric());

        metricDao.insertAll(sampleMetricEntity, otherMetricEntity);

        final MetricEntity actualValue = metricDao.getById(
                sampleMetricEntity.getMetricId() + otherMetricEntity.getMetricId());
        assertThat(actualValue, is(nullValue()));
    }

    /**
     * Asserts that if we add {@link MetricEntity}s to the {@link TraceDatabase}, then we delete all, the database
     * will be empty.
     */
    @Test
    public void deleteAll_shouldMakeEmpty() {
        final MetricEntity sampleMetricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
        final MetricEntity otherMetricEntity = new MetricEntity(MetricTestProvider.getOtherMetric());

        metricDao.insertAll(sampleMetricEntity, otherMetricEntity);
        metricDao.deleteAll();
        final int actualValue = metricDao.getAll().size();
        assertThat(actualValue, is(0));
    }
}
