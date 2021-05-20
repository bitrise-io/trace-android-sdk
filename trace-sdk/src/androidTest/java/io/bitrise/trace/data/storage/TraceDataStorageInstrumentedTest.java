package io.bitrise.trace.data.storage;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.bitrise.trace.data.management.formatter.view.ActivityStateDataFormatter;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.resource.ResourceEntity;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.data.trace.TraceEntity;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.MetricTestProvider;
import io.bitrise.trace.test.TraceTestProvider;
import io.bitrise.trace.utils.TraceClock;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.trace.v1.Span;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Contains the instrumented tests for {@link TraceDataStorage} class.
 */
public class TraceDataStorageInstrumentedTest {

  private static DataStorage dataStorage;

  /**
   * Sets up the initial state for the test class.
   */
  @BeforeClass
  public static void setUp() {
    final Context context = ApplicationProvider.getApplicationContext();
    dataStorage = TraceDataStorage.getInstance(context);
    dataStorage.traceDatabase = Room.inMemoryDatabaseBuilder(context, TraceDatabase.class).build();
    ApplicationSessionManager.getInstance().startSession();
  }

  /**
   * Tears down the required objects after all the tests run.
   */
  @AfterClass
  public static void tearDown() {
    ApplicationSessionManager.getInstance().stopSession();
    dataStorage.traceDatabase.close();
  }

  /**
   * Set up the database with the required state before each test.
   */
  @Before
  public void setUpForEach() {
    dataStorage.traceDatabase.clearAllTables();
  }

  @Test
    public void isInitialised() {
        assertTrue(DataStorage.isInitialised());
    }

    /**
     * Checks that the {@link TraceDataStorage#getInstance(Context)} returns a Singleton value.
     */
    @Test
    public void getInstance_assertIsSingleton() {
        final Context context = ApplicationProvider.getApplicationContext();
        final DataStorage expectedValue = TraceDataStorage.getInstance(context);
        final DataStorage actualValue = TraceDataStorage.getInstance(context);
        MatcherAssert.assertThat(actualValue, sameInstance(expectedValue));
    }

  @Test
    public void saveFormattedData_null() {
        dataStorage.saveFormattedData(null);
        assertEquals(0, dataStorage.getAllMetrics().size());
        assertEquals(0, dataStorage.getAllResources().size());
        assertEquals(0, dataStorage.getAllTraces().size());
    }

    @Test
    public void saveFormattedData_metric() {
        final FormattedData formattedData = new FormattedData(
                MetricTestProvider.getApplicationStartUpMetric());
        dataStorage.saveFormattedData(formattedData);
        assertEquals(1, dataStorage.getAllMetrics().size());
        assertEquals(MetricTestProvider.getApplicationStartUpMetric(),
                dataStorage.getAllMetrics().get(0).getMetric());
        assertEquals(0, dataStorage.getAllResources().size());
        assertEquals(0, dataStorage.getAllTraces().size());
    }

    @Test
    public void saveFormattedData_resource() {
        final FormattedData formattedData = new FormattedData(
                DataTestUtils.getSampleResourceEntity());
        dataStorage.saveFormattedData(formattedData);
        assertEquals(0, dataStorage.getAllMetrics().size());
        assertEquals(1, dataStorage.getAllResources().size());
        assertEquals(DataTestUtils.getSampleResourceEntity(),
                dataStorage.getAllResources().get(0));
        assertEquals(0, dataStorage.getAllTraces().size());
    }

    @Test
    public void saveFormattedData_span() {
        final FormattedData formattedData = new FormattedData(
                TraceTestProvider.createNetworkSpan());
        dataStorage.saveFormattedData(formattedData);
        assertEquals(0, dataStorage.getAllMetrics().size());
        assertEquals(0, dataStorage.getAllResources().size());
        assertEquals(0, dataStorage.getAllTraces().size());
    }

    /**
     * Asserts that if we add a {@link Metric} to the {@link TraceDatabase} via the* {@link TraceDataStorage} it will be returned when we query it.
   */
  @Test
  public void saveMetric_shouldContainInsertedValue() {
    final MetricEntity expectedValue = new MetricEntity(MetricTestProvider.getEmptyMetric());
    dataStorage.saveMetric(expectedValue);
    final MetricEntity actualValue = dataStorage.getMetricById(expectedValue.getMetricId());
    assertThat(actualValue, is(expectedValue));
  }

  /**
   * Asserts that if we add multiple {@link Metric}s to the {@link TraceDatabase} via the
   * {@link TraceDataStorage} they will be returned when we query them.
   */
  @Test
  public void getMetrics_shouldContainAllInsertedValue() {
    final MetricEntity emptyEntity = new MetricEntity(MetricTestProvider.getEmptyMetric());
    dataStorage.saveMetric(emptyEntity);
    final MetricEntity sampleEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
    dataStorage.saveMetric(sampleEntity);
    final List<MetricEntity> actualValue = dataStorage.getAllMetrics();
    assertThat(actualValue, containsInAnyOrder(emptyEntity, sampleEntity));
  }

  /**
   * Asserts that if we add a {@link Metric} to the {@link TraceDatabase} via the
   * {@link TraceDataStorage}, then delete it, it will return a {@code null} value when queried.
   */
  @Test
  public void deleteMetric_shouldDeleteValue() {
    final MetricEntity metricEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
    dataStorage.saveMetric(metricEntity);
    dataStorage.deleteMetric(metricEntity);
    final MetricEntity actualValue = dataStorage.getMetricById(metricEntity.getMetricId());
    assertThat(actualValue, is(nullValue()));
  }

  /**
   * Asserts that if we add multiple {@link MetricEntity}s to the {@link TraceDatabase} via the
   * {@link TraceDataStorage}, then delete them, they won't be found when queried.
   */
  @Test
  public void deleteMetrics_shouldDeleteMultipleValue() {
    final MetricEntity emptyEntity = new MetricEntity(MetricTestProvider.getEmptyMetric());
    dataStorage.saveMetric(emptyEntity);
    final MetricEntity sampleEntity = new MetricEntity(MetricTestProvider.getSampleMetric());
    dataStorage.saveMetric(sampleEntity);

    dataStorage.deleteMetrics(Arrays.asList(emptyEntity, sampleEntity));

    final List<MetricEntity> actualValue = dataStorage.getAllMetrics();
    assertThat(actualValue, not(containsInAnyOrder(emptyEntity, sampleEntity)));
  }

  /**
   * Asserts that if we add a {@link TraceEntity} to the {@link TraceDatabase} via the
   * {@link TraceDataStorage} it will be returned when we query it.
   */
  @Test
  public void saveTrace_shouldContainInsertedValue() {
    final Trace expectedValue = TraceTestProvider.getSampleTrace();
    dataStorage.saveTraces(expectedValue);
    final Trace actualValue = dataStorage.getTraceById(expectedValue.getTraceId());
    assertThat(actualValue, equalTo(expectedValue));
  }

  @Test
  public void saveTrace_realData() {
    final List<Span> spans = new ArrayList<>();
    long timestamp = TraceClock.getCurrentTimeMillis();
    spans.add(ActivityStateDataFormatter.createActivityViewSpan("IndexActivity", timestamp,
        timestamp + 100, "77d25914-1801-4"));
    final Trace trace = new Trace("36e817c7-8614-41", spans);

    dataStorage.saveTraces(trace);
    final Trace savedTrace = dataStorage.getTraceById("36e817c7-8614-41");

    assertThat(savedTrace, equalTo(trace));
  }

  @Test
  public void saveTrace_networkData() {
    final List<Span> spans = new ArrayList<>();
    spans.add(TraceTestProvider.createNetworkSpan());
    final Trace trace = new Trace("36e817c7-8614-41", spans);

    dataStorage.saveTraces(trace);
    final Trace savedTrace = dataStorage.getTraceById("36e817c7-8614-41");

    assertThat(savedTrace, equalTo(trace));
  }

  /**
   * Asserts that if we add multiple {@link Trace}s to the {@link TraceDatabase} via the
   * {@link TraceDataStorage} they will be returned when we query them.
   */
  @Test
  public void getTraces_shouldContainAllInsertedValue() {
    final Trace emptyTrace = TraceTestProvider.getEmptyTrace();
    dataStorage.saveTraces(emptyTrace);
    final Trace sampleTrace = TraceTestProvider.getSampleTrace();
    dataStorage.saveTraces(sampleTrace);
    final List<Trace> actualValue = dataStorage.getAllTraces();
    assertThat(actualValue, containsInAnyOrder(emptyTrace, sampleTrace));
  }
    @Test
    public void saveTrace_multipleItems() {
        final String traceId1 = "36e817c7-8614-41";
        final String traceId2 = "36e817c7-8614-42";
        final List<Span> spans1 = new ArrayList<>();
        spans1.add(TraceTestProvider.createNetworkSpan());
        final Trace trace1 = new Trace(traceId1, spans1);

        final List<Span> spans2 = new ArrayList<>();
        spans2.add(TraceTestProvider.createActivityViewSpan());
        final Trace trace2 = new Trace(traceId2, spans2);

        dataStorage.saveTraces(trace1, trace2);

        assertEquals(2, dataStorage.getAllTraces().size());
        assertEquals(trace1, dataStorage.getTraceById(traceId1));
        assertEquals(trace2, dataStorage.getTraceById(traceId2));
    }

    /**
     * Asserts that if we add multiple {@link Trace}s to the {@link TraceDatabase} via the {@link TraceDataStorage} they
     * will be returned when we query them.
     */
    @Test
    public void getTraces_shouldContainAllInsertedValue() {
        final Trace emptyTrace = TraceTestProvider.getEmptyTrace();
        dataStorage.saveTraces(emptyTrace);
        final Trace sampleTrace = TraceTestProvider.getSampleTrace();
        dataStorage.saveTraces(sampleTrace);
        final List<Trace> actualValue = dataStorage.getAllTraces();
        assertThat(actualValue, containsInAnyOrder(emptyTrace, sampleTrace));
    }

  /**
   * Asserts that if we add a {@link Trace} to the {@link TraceDatabase} via the
   * {@link TraceDataStorage}, then delete it, it will return a {@code null} value when queried.
   */
  @Test
  public void deleteTrace_shouldDeleteValue() {
    final Trace trace = TraceTestProvider.getSampleTrace();
    dataStorage.saveTraces(trace);
    dataStorage.deleteTrace(trace);
    final Trace actualValue = dataStorage.getTraceById(trace.getTraceId());
    assertThat(actualValue, is(nullValue()));
  }

  /**
   * Asserts that if we add multiple {@link Trace}s to the {@link TraceDatabase} via the
   * {@link TraceDataStorage}, then delete them, they won't be found when queried.
   */
  @Test
  public void deleteTraces_shouldDeleteMultipleValue() {
    final Trace emptyTrace = TraceTestProvider.getSampleTrace();
    dataStorage.saveTraces(emptyTrace);
    final Trace sampleTrace = TraceTestProvider.getSampleTrace();
    dataStorage.saveTraces(sampleTrace);

    dataStorage.deleteTraces(Arrays.asList(emptyTrace, sampleTrace));

    final List<Trace> actualValue = dataStorage.getAllTraces();
    assertThat(actualValue, not(containsInAnyOrder(emptyTrace, sampleTrace)));
  }

  /**
   * Asserts that if we add a {@link ResourceEntity} to the {@link TraceDatabase} via the
   * {@link TraceDataStorage} it will be returned when we query it.
   */
  @Test
  public void saveResource_shouldContainInsertedValue() {
    final ResourceEntity expectedValue = DataTestUtils.getSampleResourceEntity();
    dataStorage.saveResourceEntity(expectedValue);
    final List<ResourceEntity> actualValues =
        dataStorage.getResourcesWithSessionId(expectedValue.getSessionId());
    assertThat(actualValues, hasItem(expectedValue));
  }

  /**
   * Asserts that if we add multiple {@link ResourceEntity}s to the {@link TraceDatabase} via the
   * {@link TraceDataStorage} they will be returned when we query them.
   */
  @Test
  public void getResources_shouldContainAllInsertedValue() {
    final ResourceEntity sampleResourceEntity = DataTestUtils.getSampleResourceEntity();
    dataStorage.saveResourceEntity(sampleResourceEntity);
    final ResourceEntity otherResourceEntity = DataTestUtils.getOtherResourceEntity();
    dataStorage.saveResourceEntity(otherResourceEntity);
    final List<ResourceEntity> actualValue = dataStorage.getAllResources();
    assertThat(actualValue, containsInAnyOrder(sampleResourceEntity, otherResourceEntity));
  }

  /**
   * Asserts that if we add a {@link ResourceEntity} to the {@link TraceDatabase} via the
   * {@link TraceDataStorage}, then delete it, it will return a {@code null} value when queried.
   */
  @Test
  public void deleteResource_shouldDeleteValue() {
    final ResourceEntity resourceEntity = DataTestUtils.getSampleResourceEntity();
    dataStorage.saveResourceEntity(resourceEntity);
    dataStorage.deleteResourcesWithSessionId(resourceEntity.getSessionId());
    final int actualValue =
        dataStorage.getResourcesWithSessionId(resourceEntity.getSessionId()).size();
    assertThat(actualValue, is(0));
  }

  /**
   * Asserts that if we add multiple {@link ResourceEntity}s to the {@link TraceDatabase} via the
   * {@link TraceDataStorage}, then delete them, they won't be found when queried.
   */
  @Test
  public void deleteResource_shouldDeleteMultipleValue() {
    final ResourceEntity sampleResourceEntity = DataTestUtils.getSampleResourceEntity();
    dataStorage.saveResourceEntity(sampleResourceEntity);
    final ResourceEntity otherResourceEntity = DataTestUtils.getOtherResourceEntity();
    dataStorage.saveResourceEntity(otherResourceEntity);

    dataStorage.deleteResources(Arrays.asList(sampleResourceEntity, otherResourceEntity));

    final List<ResourceEntity> actualValue = dataStorage.getAllResources();
    assertThat(actualValue, not(containsInAnyOrder(sampleResourceEntity, otherResourceEntity)));
  }

    @Test
    public void getFirstTraceGroup_noSessions() {
        assertEquals(Collections.emptyList(), dataStorage.getFirstTraceGroup());
    }

    @Test
    public void getFirstMetricGroup_noSessions() {
        assertEquals(Collections.emptyList(), dataStorage.getFirstMetricGroup());
    }

    @Test
    public void hasReference_metricAndTraces() {
        final String sessionId = "01F0VDZZJQKVKX01A2XQR91F49";

        final MetricEntity metricEntity = new MetricEntity("metricId");
        metricEntity.setMetric(MetricTestProvider.getSystemCpuMetric());
        metricEntity.setSessionId(sessionId);
        dataStorage.saveMetric(metricEntity);

        final List<Span> spans = new ArrayList<>();
        spans.add(TraceTestProvider.createNetworkSpan());
        final Trace trace = new Trace("36e817c7-8614-41", spans);
        trace.setSessionId(sessionId);
        dataStorage.saveTraces(trace);

        assertTrue(dataStorage.hasReference(sessionId));
    }

    @Test
    public void hasReference_metricOnly() {
        final String sessionId = "01F0VDZZJQKVKX01A2XQR91F49";

        final MetricEntity metricEntity = new MetricEntity("metricId");
        metricEntity.setMetric(MetricTestProvider.getSystemCpuMetric());
        metricEntity.setSessionId(sessionId);
        dataStorage.saveMetric(metricEntity);

        assertTrue(dataStorage.hasReference(sessionId));
    }

    @Test
    public void hasReference_tracesOnly() {
        final String sessionId = "01F0VDZZJQKVKX01A2XQR91F49";

        final List<Span> spans = new ArrayList<>();
        spans.add(TraceTestProvider.createNetworkSpan());
        final Trace trace = new Trace("36e817c7-8614-41", spans);
        trace.setSessionId(sessionId);
        dataStorage.saveTraces(trace);

        assertTrue(dataStorage.hasReference(sessionId));
    }

    @Test
    public void hasReference_noReferences() {
        final String sessionId = "01F0VDZZJQKVKX01A2XQR91F49";
        assertFalse(dataStorage.hasReference(sessionId));
    }

    @Test
    public void deleteAllResources() {
        final Resource resource = DataTestUtils.getSampleResource("sessionId");
        dataStorage.saveResource(resource);
        assertEquals(resource.getLabelsCount(), dataStorage.getAllResources().size());
        dataStorage.deleteAllResources();
        assertEquals(0, dataStorage.getAllResources().size());
    }

    @Test
    public void setTraceDatabase() {
        final TraceDatabase mockDatabase = Mockito.mock(TraceDatabase.class);
        dataStorage.setTraceDatabase(mockDatabase);
        assertEquals(mockDatabase, dataStorage.traceDatabase);
    }
}