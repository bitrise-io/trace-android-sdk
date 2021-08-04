package io.bitrise.trace;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.Context;
import io.bitrise.trace.configuration.ConfigurationManager;
import io.bitrise.trace.data.management.DataManager;
import io.bitrise.trace.session.ApplicationSessionManager;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.utils.log.AndroidLogger;
import io.bitrise.trace.utils.log.ErrorOnlyLogger;
import io.bitrise.trace.utils.log.TraceLog;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test cases for {@link TraceSdk}.
 */
public class TraceSdkTest {

  @Before
  public void before() {
    TraceSdk.reset();
  }

  @Test
  public void reset() {
    TraceSdk.traceSdk = Mockito.mock(TraceSdk.class);
    TraceSdk.reset();
    assertNull(TraceSdk.traceSdk);
    assertFalse(TraceSdk.isInitialised());
  }

  @Test
  public void initSessionManager_SessionShouldNotBeNull() {
    TraceSdk.initSessionManager();
    final Session actualResult = ApplicationSessionManager.getInstance().getActiveSession();
    assertThat(actualResult, is(notNullValue()));
  }

  @Test
  public void initLogger_debugMode() {
    ConfigurationManager.getDebugInstance("token", Collections.singletonMap("DEBUG", true));
    TraceSdk.initLogger();

    assertTrue(ConfigurationManager.getInstance().isAppDebugBuild());
    assertTrue(TraceLog.getLogger() instanceof AndroidLogger);
  }

  @Test
  public void initLogger_notDebugMode() {
    ConfigurationManager.getDebugInstance("token", Collections.singletonMap("DEBUG", false));
    TraceSdk.initLogger();

    assertFalse(ConfigurationManager.getInstance().isAppDebugBuild());
    assertTrue(TraceLog.getLogger() instanceof ErrorOnlyLogger);
  }

  @Test
  public void initDataCollection() {
    final DataManager mockDataManager = Mockito.mock(DataManager.class);
    DataManager.setTestInstance(mockDataManager);
    final Context mockContext = Mockito.mock(Context.class);

    TraceSdk.initDataCollection(mockContext);

    verify(mockDataManager, times(1)).startCollection(mockContext);
    verify(mockDataManager, times(1)).startSending(mockContext);
  }

  @Test
  public void initLifeCycleListener_fromContextApplication() {
    final Application mockApplication = Mockito.mock(Application.class);
    TraceSdk.initLifeCycleListener((Context) mockApplication);

    verify(mockApplication, times(1))
        .registerActivityLifecycleCallbacks(any());
  }

  @Test
  public void initLifeCycleListener_fromContextGetApplicationContext() {
    final Application mockApplication = Mockito.mock(Application.class);
    final Context mockContext = Mockito.mock(Context.class);

    when(mockContext.getApplicationContext()).thenReturn(mockApplication);
    TraceSdk.initLifeCycleListener(mockContext);

    verify(mockApplication, times(1))
        .registerActivityLifecycleCallbacks(any());
  }

  @Test
  public void initLifeCycleListener_noApplicationFromContext() {
    final Context mockContext = Mockito.mock(Context.class);

    TraceSdk.initLifeCycleListener(mockContext);

    verify(mockContext, times(1)).getApplicationContext();
    verifyNoMoreInteractions(mockContext);
  }

}