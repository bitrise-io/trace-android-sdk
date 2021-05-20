package io.bitrise.trace.network;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.content.res.Resources;
import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ApplicationProvider;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TestDataStorage;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.data.trace.TraceEntity;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.TraceTestProvider;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Contains the tests for {@link TraceSender}.
 */
public class TraceSenderInstrumentedTest {

  private static DataStorage dataStorage;
  private static Context context;

  /**
   * Sets up the initial state for the test class.
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    TestNetworkClient.reset();
    context = ApplicationProvider.getApplicationContext();
    dataStorage = TestDataStorage.getInstance(context);
    ApplicationSessionManager.getInstance().startSession();
  }

  /**
   * Tears down the required objects after all the tests run.
   */
  @AfterClass
  public static void tearDownClass() {
    ApplicationSessionManager.getInstance().stopSession();
  }

  /**
   * Sets up the initial state for each test case.
   */
  @Before
  public void setUpBefore() throws InterruptedException {
    final CountDownLatch countDownLatch = new CountDownLatch(1);
    final Thread thread = new Thread(() -> {
      dataStorage.deleteAllTraces();
      final Trace sampleTrace = TraceTestProvider.getSampleTrace();
      dataStorage.saveTraces(sampleTrace);
      dataStorage.saveResource(DataTestUtils.getSampleResource(sampleTrace.getSessionId()));
      countDownLatch.countDown();
    });
    thread.start();
    countDownLatch.await();
  }

  /**
   * When the {@link TraceSender} is started and it gets a HTTP 200 as result, it should be
   * success.
   *
   * @throws ExecutionException   if the computation threw an exception.
   * @throws InterruptedException if the current thread was interrupted while waiting.
   */
  @UiThreadTest
  @Test
  public void send_shouldBeResultSuccess() throws ExecutionException, InterruptedException {
    final TraceSender traceSender = mock(TraceSender.class, Mockito.CALLS_REAL_METHODS);
    doNothing().when(traceSender).onSendingFinished(any(), anyBoolean());
    traceSender.setNetworkCommunicator(TestNetworkClient.getSuccessCommunicator());
    traceSender.setDataStorage(dataStorage);

    final DataSender.Result expectedResult = DataSender.Result.SUCCESS;
    final DataSender.Result actualResult = traceSender.send(null).get();
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * When the {@link TraceSender} is started and it gets a HTTP 400 as result, it should be
   * failure.
   *
   * @throws ExecutionException   if the computation threw an exception.
   * @throws InterruptedException if the current thread was interrupted while waiting.
   */
  @UiThreadTest
  @Test
  public void send_shouldBeResultFailure() throws ExecutionException, InterruptedException {
    final TraceSender traceSender = mock(TraceSender.class, Mockito.CALLS_REAL_METHODS);
    doNothing().when(traceSender).onSendingFinished(any(), anyBoolean());
    traceSender.setNetworkCommunicator(TestNetworkClient.getFailingCommunicator());
    traceSender.setDataStorage(dataStorage);

    final DataSender.Result expectedResult = DataSender.Result.FAILURE;
    final DataSender.Result actualResult = traceSender.send(null).get();
    assertThat(actualResult, is(expectedResult));
  }

  /**
   * When the {@link TraceSender} is started and it gets an IOException.
   *
   * @throws ExecutionException   if the computation threw an exception.
   * @throws InterruptedException if the current thread was interrupted while waiting.
   */
  @UiThreadTest
  @Test(expected = IOException.class)
  public void send_shouldBeResultException() throws Throwable {
    final TraceSender traceSender = mock(TraceSender.class, Mockito.CALLS_REAL_METHODS);
    doNothing().when(traceSender).onSendingFinished(any(), anyBoolean());
    traceSender.setNetworkCommunicator(TestNetworkClient.getExceptionCommunicator());
    traceSender.setDataStorage(dataStorage);

    try {
      traceSender.send(null).get();
    } catch (final ExecutionException e) {
      throw e.getCause();
    }
  }

  /**
   * If the {@link TraceEntity} is sent, and the response is success, the {@link DataStorage}
   * should delete the given TraceEntity.
   *
   * @throws ExecutionException   if the computation threw an exception.
   * @throws InterruptedException if the current thread was interrupted while waiting.
   */
  @Test
  public void send_traceShouldBeDeletedOnSuccess() throws ExecutionException, InterruptedException {
    final TraceSender traceSender = mock(TraceSender.class, Mockito.CALLS_REAL_METHODS);
    doNothing().when(traceSender).onSendingFinished(any(), anyBoolean());
    traceSender.setNetworkCommunicator(TestNetworkClient.getSuccessCommunicator());
    traceSender.setDataStorage(dataStorage);

    final Trace dummyTrace = TraceTestProvider.getEmptyTrace();
    traceSender.getDataStorage().saveTraces(dummyTrace);
    traceSender.send(null).get();
    final Trace actualResult = traceSender.getDataStorage().getTrace(new TraceEntity(dummyTrace));

    assertThat(actualResult, is(nullValue()));
  }

  /**
   * If the {@link TraceEntity}s is sent, but the response is failure, the {@link DataStorage}
   * should still hold it.
   *
   * @throws ExecutionException   if the computation threw an exception.
   * @throws InterruptedException if the current thread was interrupted while waiting.
   */
  @Test
  public void send_traceShouldNotBeDeletedOnFailure()
      throws ExecutionException, InterruptedException {
    final TraceSender traceSender = mock(TraceSender.class, Mockito.CALLS_REAL_METHODS);
    doNothing().when(traceSender).onSendingFinished(any(), anyBoolean());
    traceSender.setNetworkCommunicator(TestNetworkClient.getFailingCommunicator());
    traceSender.setDataStorage(dataStorage);

    final Trace dummyTrace = TraceTestProvider.getSampleTrace();
    traceSender.getDataStorage().saveTraces(dummyTrace);
    traceSender.send(null).get();
    final Trace actualResult = traceSender.getDataStorage().getTrace(new TraceEntity(dummyTrace));

    assertThat(actualResult, is(dummyTrace));
  }

  /**
   * When the {@link ConfigurationManager} has not been initialised, or been shut down, the
   * sender should initialise it during it's creation.
   */
  @Test
  public void onCreate_shouldInitialiseConfigurationManager() {
    ConfigurationManager.reset();
    final TraceSender traceSender = new TraceSender();
    traceSender.setContext(context);
    try {
      traceSender.onCreate();
    } catch (final Resources.NotFoundException e) {
      // intended, SDK misses the resource, only apps can have it
    }

    assertThat(ConfigurationManager.isInitialised(), is(true));
  }

  /**
   * When only empty {@link Trace}s are present, {@link TraceSender#getNetworkRequest()} should
   * filter it out.
   */
  @Test
  public void getNetworkRequest_ShouldFilterEmptyTraces() {
    final TraceSender traceSender = new TraceSender();
    traceSender.setNetworkCommunicator(TestNetworkClient.getSuccessCommunicator());
    traceSender.setDataStorage(dataStorage);
    dataStorage.deleteAllTraces();

    traceSender.getDataStorage().saveTraces(TraceTestProvider.getEmptyTrace());
    final NetworkRequest actualValue = traceSender.getNetworkRequest();
    assertThat(actualValue, is(nullValue()));
  }
}