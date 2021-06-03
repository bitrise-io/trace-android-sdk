package io.bitrise.trace.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.job.JobParameters;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.test.DataTestUtils;
import io.opencensus.proto.resource.v1.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link DataSender}.
 */
public class DataSenderTest {

  private final MetricSender metricSender = new MetricSender();
  final JobParameters mockJobParameters = Mockito.mock(JobParameters.class);
  private final DataStorage mockDataStorage = Mockito.mock(DataStorage.class);
  private static final ApplicationSessionManager mockSessionManager =
      Mockito.mock(ApplicationSessionManager.class);

  @BeforeClass
  public static void beforeClass() {
    ApplicationSessionManager.setTestInstance(mockSessionManager);
  }
  
  @AfterClass
  public static void afterClass() {
    ApplicationSessionManager.reset();
  }

  @Test
  public void validateNetworkRequest_nullNetworkRequest() {
    assertFalse(DataSender.validateNetworkRequest(null));
  }

  @Test
  public void validateNetworkRequest_hasAllRequiredLabels() {
    ConfigurationManager.getDebugInstance("token", new HashMap<>());

    MetricRequest metricRequest = new MetricRequest(
        DataTestUtils.getSampleResource("sessionId"), new ArrayList<>());

    assertTrue(DataSender.validateNetworkRequest(metricRequest));
  }

  @Test
  public void validateNetworkRequest_missingRequiredLabels() {
    ConfigurationManager.getDebugInstance("token", new HashMap<>());

    MetricRequest metricRequest = new MetricRequest(
        Resource.newBuilder().build(), new ArrayList<>());

    assertFalse(DataSender.validateNetworkRequest(metricRequest));
  }

  @Test
  public void onStopJob() {
    metricSender.setStopped(false);
    assertFalse(metricSender.isStopped());

    metricSender.onStopJob(mockJobParameters);
    assertTrue(metricSender.isStopped());
  }

  @Test
  public void removeResources_activeSessionNull_noReferences() {
    final String sessionId = "sessionId";

    when(mockSessionManager.getActiveSession()).thenReturn(null);
    when(mockDataStorage.hasReference(sessionId)).thenReturn(false);
    metricSender.setDataStorage(mockDataStorage);

    assertTrue(metricSender.removeResources(sessionId));
    verify(mockDataStorage, times(1))
        .deleteResourcesWithSessionId(sessionId);
  }

  @Test
  public void removeResources_activeSessionNull_hasReferences() {
    final String sessionId = "sessionId";
    when(mockSessionManager.getActiveSession()).thenReturn(null);
    when(mockDataStorage.hasReference(sessionId)).thenReturn(true);
    metricSender.setDataStorage(mockDataStorage);

    assertFalse(metricSender.removeResources(sessionId));
  }

  @Test
  public void removeResources_activeSessionDifferent_noReferences() {
    final String sessionId = "sessionId";
    final String activeSessionId = "activeSessionId";

    when(mockSessionManager.getActiveSession()).thenReturn(new Session(activeSessionId));
    when(mockDataStorage.hasReference(sessionId)).thenReturn(false);
    metricSender.setDataStorage(mockDataStorage);

    assertTrue(metricSender.removeResources(sessionId));
    verify(mockDataStorage, times(1))
        .deleteResourcesWithSessionId(sessionId);
  }

  @Test
  public void removeResources_activeSessionDifferent_hasReferences() {
    final String sessionId = "sessionId";
    final String activeSessionId = "activeSessionId";

    when(mockSessionManager.getActiveSession()).thenReturn(new Session(activeSessionId));
    when(mockDataStorage.hasReference(sessionId)).thenReturn(true);
    metricSender.setDataStorage(mockDataStorage);

    assertFalse(metricSender.removeResources(sessionId));
  }

  @Test
  public void removeResources_sameSession_noReferences() {
    final String sessionId = "sessionId";

    when(mockSessionManager.getActiveSession()).thenReturn(new Session(sessionId));
    when(mockDataStorage.hasReference(sessionId)).thenReturn(false);
    metricSender.setDataStorage(mockDataStorage);

    assertFalse(metricSender.removeResources(sessionId));
  }

  @Test
  public void removeResources_sameSession_hasReferences() {
    final String sessionId = "sessionId";

    when(mockSessionManager.getActiveSession()).thenReturn(new Session(sessionId));
    when(mockDataStorage.hasReference(sessionId)).thenReturn(true);
    metricSender.setDataStorage(mockDataStorage);

    assertFalse(metricSender.removeResources(sessionId));
  }

  @Test
  public void getResultSettableFuture() {
    metricSender.resultSettableFuture = null;
    assertNotNull(metricSender.getResultSettableFuture());
  }

  @Test
  public void getNetworkCommunicator() {
    metricSender.networkCommunicator = null;
    assertNotNull(metricSender.getNetworkCommunicator());
  }

  @Test
  public void getContext() {
    metricSender.context = null;
    assertNotNull(metricSender.getContext());
  }

  @Test
  public void setExecutor() {
    metricSender.executor = null;
    final ExecutorService mockService = Mockito.mock(ExecutorService.class);
    metricSender.setExecutor(mockService);
    assertEquals(mockService, metricSender.getExecutor());
  }

}
