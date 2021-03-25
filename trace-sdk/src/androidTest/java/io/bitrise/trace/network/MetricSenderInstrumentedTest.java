package io.bitrise.trace.network;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ApplicationProvider;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import io.bitrise.trace.InstrumentedTestRequirements;
import io.bitrise.trace.TestKitTest;
import io.bitrise.trace.TestKitUtils;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.collector.cpu.SystemCpuUsageDataCollector;
import io.bitrise.trace.data.collector.memory.ApplicationUsedMemoryDataCollector;
import io.bitrise.trace.data.collector.memory.SystemMemoryDataCollector;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.DataFormatterDelegator;
import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TestDataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.MetricTestProvider;
import io.opencensus.proto.metrics.v1.Metric;
import retrofit2.Response;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * Contains the tests for {@link MetricSender}.
 */
public class MetricSenderInstrumentedTest {

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
            dataStorage.deleteAllMetrics();
            final Metric sampleMetric = MetricTestProvider.getSampleMetric();
            ApplicationSessionManager.getInstance().startSession();
            final MetricEntity metricEntity = new MetricEntity(sampleMetric);
            dataStorage.saveMetric(metricEntity);
            dataStorage.saveResource(DataTestUtils.getSampleResource(metricEntity.getSessionId()));
            countDownLatch.countDown();
        });
        thread.start();
        countDownLatch.await();
    }

    /**
     * When the {@link MetricSender} is started and it gets a HTTP 200 as result, it should be success.
     *
     * @throws ExecutionException   if the computation threw an exception.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     */
    @UiThreadTest
    @Test
    public void send_shouldBeResultSuccess() throws ExecutionException, InterruptedException {
        final MetricSender metricSender = mock(MetricSender.class, Mockito.CALLS_REAL_METHODS);
        doNothing().when(metricSender).onSendingFinished(any(), anyBoolean());
        metricSender.setNetworkCommunicator(TestNetworkClient.getSuccessCommunicator());
        metricSender.setDataStorage(dataStorage);

        final DataSender.Result expectedResult = DataSender.Result.SUCCESS;
        final DataSender.Result actualResult = metricSender.send(null).get();
        assertThat(actualResult, is(expectedResult));
    }

    /**
     * When the {@link MetricSender} is started and it gets a HTTP 400 as result, it should be failure.
     *
     * @throws ExecutionException   if the computation threw an exception.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     */
    @UiThreadTest
    @Test
    public void send_shouldBeResultFailure() throws ExecutionException, InterruptedException {
        final MetricSender metricSender = mock(MetricSender.class, Mockito.CALLS_REAL_METHODS);
        doNothing().when(metricSender).onSendingFinished(any(), anyBoolean());
        metricSender.setNetworkCommunicator(TestNetworkClient.getFailingCommunicator());
        metricSender.setDataStorage(dataStorage);

        final DataSender.Result expectedResult = DataSender.Result.FAILURE;
        final DataSender.Result actualResult = metricSender.send(null).get();
        assertThat(actualResult, is(expectedResult));
    }

    /**
     * When the {@link MetricSender} is started and it gets an IOException..
     *
     * @throws ExecutionException   if the computation threw an exception.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     */
    @UiThreadTest
    @Test(expected = IOException.class)
    public void send_shouldBeResultException() throws Throwable {
        final MetricSender metricSender = mock(MetricSender.class, Mockito.CALLS_REAL_METHODS);
        doNothing().when(metricSender).onSendingFinished(any(), anyBoolean());
        metricSender.setNetworkCommunicator(TestNetworkClient.getExceptionCommunicator());
        metricSender.setDataStorage(dataStorage);

        try {
            metricSender.send(null).get();
        } catch (final ExecutionException e) {
            throw e.getCause();
        }
    }

    /**
     * If the {@link MetricEntity} is sent, and the response is success, the {@link DataStorage} should delete the
     * given MetricEntity.
     *
     * @throws ExecutionException   if the computation threw an exception.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     */
    @Test
    public void send_metricShouldBeDeletedOnSuccess() throws ExecutionException, InterruptedException {
        final MetricSender metricSender = mock(MetricSender.class, Mockito.CALLS_REAL_METHODS);
        doNothing().when(metricSender).onSendingFinished(any(), anyBoolean());
        metricSender.setNetworkCommunicator(TestNetworkClient.getSuccessCommunicator());
        metricSender.setDataStorage(dataStorage);

        final MetricEntity dummyMetric = new MetricEntity(MetricTestProvider.getEmptyMetric());
        metricSender.getDataStorage().saveMetric(dummyMetric);
        metricSender.send(null).get();
        final MetricEntity actualResult = metricSender.getDataStorage().getMetricById(dummyMetric.getMetricId());

        assertThat(actualResult, is(nullValue()));
    }

    /**
     * If the {@link MetricEntity}s is sent, but the response is failure, the {@link DataStorage} should still hold it.
     *
     * @throws ExecutionException   if the computation threw an exception.
     * @throws InterruptedException if the current thread was interrupted while waiting.
     */
    @Test
    public void send_metricShouldNotBeDeletedOnFailure() throws ExecutionException, InterruptedException {
        final MetricSender metricSender = mock(MetricSender.class, Mockito.CALLS_REAL_METHODS);
        doNothing().when(metricSender).onSendingFinished(any(), anyBoolean());
        metricSender.setNetworkCommunicator(TestNetworkClient.getFailingCommunicator());
        metricSender.setDataStorage(dataStorage);

        final MetricEntity dummyMetric = new MetricEntity(MetricTestProvider.getEmptyMetric());
        metricSender.getDataStorage().saveMetric(dummyMetric);
        metricSender.send(null).get();
        final MetricEntity actualResult = metricSender.getDataStorage().getMetricById(dummyMetric.getMetricId());

        assertThat(actualResult, is(dummyMetric));
    }

    /**
     * When the {@link ConfigurationManager} has not been initialised, or been shut down, the sender should
     * initialise it during it's creation.
     */
    @Test
    public void onCreate_shouldInitialiseConfigurationManager() {
        ConfigurationManager.reset();
        final MetricSender metricSender = new MetricSender();
        metricSender.setContext(context);
        try {
            metricSender.onCreate();
        } catch (final Resources.NotFoundException e) {
            // intended, SDK misses the resource, only apps can have it
        }

        assertThat(ConfigurationManager.isInitialised(), is(true));
    }

    /**
     * Tests when the {@link ApplicationUsedMemoryDataCollector}s collected and sent to the TestKit, the result
     * should be {@link HttpURLConnection#HTTP_ACCEPTED}.
     *
     * @throws IOException if any I/O exception occurs.
     */
    @TestKitTest
    @Test
    public void testKit_sendApplicationUsedMemoryMetric() throws IOException {
        InstrumentedTestRequirements.assumeTestKitApiLevel();

        final NetworkCommunicator networkCommunicator = TestKitUtils.getNetworkCommunicatorWithValidToken();
        final ApplicationUsedMemoryDataCollector applicationUsedMemoryDataCollector =
                new ApplicationUsedMemoryDataCollector(context);
        final Data data = applicationUsedMemoryDataCollector.collectData();
        final FormattedData[] formattedData = DataFormatterDelegator.getInstance().formatData(data);
        final Metric metric = formattedData[0].getMetricEntity().getMetric();

        final MetricRequest metricRequest =
                DataTestUtils.getMetricRequest(Collections.singletonList(metric));
        final Response<Void> response =
                networkCommunicator.sendMetrics(metricRequest).execute();
        final int actualValue = response.code();
        assertThat(actualValue, is(HttpURLConnection.HTTP_ACCEPTED));
    }

    /**
     * Tests when the {@link SystemMemoryDataCollector}s collected and sent to the TestKit, the result should be
     * {@link HttpURLConnection#HTTP_ACCEPTED}.
     *
     * @throws IOException if any I/O exception occurs.
     */
    @TestKitTest
    @Test
    public void testKit_sendTotalUsedMemoryMetric() throws IOException {
        InstrumentedTestRequirements.assumeTestKitApiLevel();

        final NetworkCommunicator networkCommunicator = TestKitUtils.getNetworkCommunicatorWithValidToken();
        final SystemMemoryDataCollector systemMemoryDataCollector = new SystemMemoryDataCollector(context);
        final Data data = systemMemoryDataCollector.collectData();
        final FormattedData[] formattedData = DataFormatterDelegator.getInstance().formatData(data);
        final Metric metric = formattedData[0].getMetricEntity().getMetric();

        final MetricRequest metricRequest =
                DataTestUtils.getMetricRequest(Collections.singletonList(metric));
        final Response<Void> response =
                networkCommunicator.sendMetrics(metricRequest).execute();
        final int actualValue = response.code();
        assertThat(actualValue, is(HttpURLConnection.HTTP_ACCEPTED));
    }

    /**
     * Tests when the {@link SystemCpuUsageDataCollector}s collected system CPU usage and sent to the TestKit, the
     * result
     * should be {@link HttpURLConnection#HTTP_ACCEPTED}.
     *
     * @throws IOException if any I/O exception occurs.
     */
    @TestKitTest
    @Test
    public void testKit_sendSystemCpuUsageMetric() throws IOException {
        InstrumentedTestRequirements.assumeTestKitApiLevel();
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final NetworkCommunicator networkCommunicator = TestKitUtils.getNetworkCommunicatorWithValidToken();
        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final Data data = systemCpuUsageDataCollector.collectData();
        final FormattedData[] formattedData = DataFormatterDelegator.getInstance().formatData(data);
        final Metric metric = formattedData[0].getMetricEntity().getMetric();

        final MetricRequest metricRequest =
                DataTestUtils.getMetricRequest(Collections.singletonList(metric));
        final Response<Void> response =
                networkCommunicator.sendMetrics(metricRequest).execute();
        final int actualValue = response.code();
        assertThat(actualValue, is(HttpURLConnection.HTTP_ACCEPTED));
    }

    /**
     * Tests when the {@link SystemCpuUsageDataCollector}s collected application CPU usage and sent to the TestKit, the
     * result should be {@link HttpURLConnection#HTTP_ACCEPTED}.
     *
     * @throws IOException if any I/O exception occurs.
     */
    @TestKitTest
    @Test
    public void testKit_sendApplicationCpuUsageMetric() throws IOException {
        InstrumentedTestRequirements.assumeTestKitApiLevel();
        InstrumentedTestRequirements.assumeCpuApiLevel();

        final NetworkCommunicator networkCommunicator = TestKitUtils.getNetworkCommunicatorWithValidToken();
        final SystemCpuUsageDataCollector systemCpuUsageDataCollector = new SystemCpuUsageDataCollector();
        final Data data = systemCpuUsageDataCollector.collectData();
        final FormattedData[] formattedData = DataFormatterDelegator.getInstance().formatData(data);
        final Metric metric = formattedData[1].getMetricEntity().getMetric();

        final MetricRequest metricRequest =
                DataTestUtils.getMetricRequest(Collections.singletonList(metric));
        final Response<Void> response =
                networkCommunicator.sendMetrics(metricRequest).execute();
        final int actualValue = response.code();
        assertThat(actualValue, is(HttpURLConnection.HTTP_ACCEPTED));
    }
}