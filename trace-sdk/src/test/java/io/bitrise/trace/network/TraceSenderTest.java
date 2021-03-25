package io.bitrise.trace.network;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.bitrise.trace.data.collector.device.DeviceOsVersionDataCollector;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.test.TraceTestProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for {@link TraceSender}.
 */
public class TraceSenderTest {

    private static final DataManager mockDataManager = mock(DataManager.class);
    private static final TraceSender traceSender = new TraceSender();
    private static final DataStorage mockDataStorage = Mockito.mock(DataStorage.class);

    /**
     * Sets up the initial state for the test class.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        ApplicationSessionManager.getInstance().startSession();
        traceSender.setDataStorage(mockDataStorage);
        traceSender.setDataManager(mockDataManager);
    }

    @Test
    public void findEmptyTraces_emptyList() {
        final List<Trace> inputTraces = new ArrayList<>();
        final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);
        assertEquals(0, outputTraces.size());
    }

    @Test
    public void findEmptyTraces_nullList() {
        final List<Trace> outputTraces = traceSender.findEmptyTraces(null);
        assertEquals(0, outputTraces.size());
    }

    @Test
    public void findEmptyTraces_twoValidTraces() {
        final List<Trace> inputTraces = new ArrayList<>();
        inputTraces.add(TraceTestProvider.getSampleTrace());
        inputTraces.add(TraceTestProvider.getOtherTrace());

        final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);

        assertEquals(0, outputTraces.size());
    }

    @Test
    public void findEmptyTraces_twoEmptyTraces() {
        final List<Trace> inputTraces = new ArrayList<>();
        inputTraces.add(TraceTestProvider.getEmptyTrace());
        inputTraces.add(TraceTestProvider.getEmptyTrace());

        final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);

        assertEquals(2, outputTraces.size());
    }

    @Test
    public void findEmptyTraces_twoValidTraces_andTwoEmptyTraces() {
        final List<Trace> inputTraces = new ArrayList<>();
        inputTraces.add(TraceTestProvider.getSampleTrace());
        inputTraces.add(TraceTestProvider.getOtherTrace());
        inputTraces.add(TraceTestProvider.getEmptyTrace());
        inputTraces.add(TraceTestProvider.getEmptyTrace());

        final List<Trace> outputTraces = traceSender.findEmptyTraces(inputTraces);

        assertEquals(2, outputTraces.size());
    }

    @Test
    public void needReschedule_shouldBeFalse() {
        when(mockDataStorage.getAllMetrics()).thenReturn(Collections.emptyList());
        when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
        when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

        assertFalse(traceSender.isRescheduleNeeded());
    }

    @Test
    public void needReschedule_shouldBeTrue_hasTraces() {
        when(mockDataStorage.getAllTraces()).thenReturn(Collections.singletonList(TraceTestProvider.getEmptyTrace()));
        when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
        when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

        assertTrue(traceSender.isRescheduleNeeded());
    }

    @Test
    public void needReschedule_shouldBeTrue_hasActiveCollector() {
        when(mockDataStorage.getAllTraces()).thenReturn(Collections.emptyList());
        when(mockDataManager.getActiveDataCollectors()).thenReturn(
                Collections.singleton(new DeviceOsVersionDataCollector()));
        when(mockDataManager.getActiveDataListeners()).thenReturn(Collections.emptySet());

        assertTrue(traceSender.isRescheduleNeeded());
    }

    @Test
    public void needReschedule_shouldBeTrue_hasActiveListener() {
        when(mockDataStorage.getAllTraces()).thenReturn(Collections.emptyList());
        when(mockDataManager.getActiveDataCollectors()).thenReturn(Collections.emptySet());
        when(mockDataManager.getActiveDataListeners()).thenReturn(
                Collections.singleton(mock(OkHttpDataListener.class)));

        assertTrue(traceSender.isRescheduleNeeded());
    }

    @Test
    public void needReschedule_shouldBeTrue_hasAll() {
        when(mockDataStorage.getAllTraces()).thenReturn(Collections.singletonList(TraceTestProvider.getEmptyTrace()));
        when(mockDataManager.getActiveDataCollectors()).thenReturn(
                Collections.singleton(new DeviceOsVersionDataCollector()));
        when(mockDataManager.getActiveDataListeners()).thenReturn(
                Collections.singleton(mock(OkHttpDataListener.class)));

        assertTrue(traceSender.isRescheduleNeeded());
    }
}
