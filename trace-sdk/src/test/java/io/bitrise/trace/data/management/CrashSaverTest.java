package io.bitrise.trace.data.management;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import io.bitrise.trace.data.CrashTestDataProvider;
import io.bitrise.trace.data.storage.TraceDataStorage;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.test.DataTestUtils;
import io.bitrise.trace.test.TraceTestProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link CrashSaver}.
 */
public class CrashSaverTest {

  final TraceDataStorage mockDataStorage = Mockito.mock(TraceDataStorage.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    ApplicationSessionManager.getInstance().startSession();
  }

  @AfterClass
  public static void tearDownClass() {
    ApplicationSessionManager.getInstance().stopSession();
  }

  @Test
  public void saveCrash_validData() {
    CrashSaver.saveCrash(
        DataTestUtils.getSampleResource("session-id"),
        new Session("session-id"),
        TraceTestProvider.getEmptyTrace(),
        CrashTestDataProvider.createCrashReport(),
        mockDataStorage);

    verify(mockDataStorage, times(1)).saveCrashRequest(any());
  }

  @Test
  public void saveCrash_nullSession() {
    CrashSaver.saveCrash(
        DataTestUtils.getSampleResource("session-id"),
        null,
        TraceTestProvider.getEmptyTrace(),
        CrashTestDataProvider.createCrashReport(),
        mockDataStorage);

    verifyNoInteractions(mockDataStorage);
  }

  @Test
  public void saveCrash_nullResource() {
    CrashSaver.saveCrash(
        null,
        new Session("session-id"),
        TraceTestProvider.getEmptyTrace(),
        CrashTestDataProvider.createCrashReport(),
        mockDataStorage);

    verifyNoInteractions(mockDataStorage);
  }

  @Test
  public void saveCrash_nullActiveResource() {
    CrashSaver.saveCrash(
        DataTestUtils.getSampleResource("session-id"),
        new Session("session-id"),
        null,
        CrashTestDataProvider.createCrashReport(),
        mockDataStorage);

    verifyNoInteractions(mockDataStorage);
  }
}
