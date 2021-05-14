package io.bitrise.trace.data.trace;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.google.protobuf.ByteString;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.storage.TraceDataStorage;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.trace.v1.Span;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import javax.inject.Singleton;

/**
 * Implementation for {@link TraceManager}.
 */
@Singleton
public class ApplicationTraceManager implements TraceManager {

  @NonNull
  private static final Object applicationTraceManagerLock = new Object();
  @NonNull
  private static final Object activeTraceLock = new Object();
  @Nullable
  private static volatile ApplicationTraceManager applicationTraceManager;
  @VisibleForTesting
  @NonNull
  DataStorage dataStorage;
  @Nullable
  private Trace activeTrace;
  @Nullable
  private String rootSpanId;

  /**
   * Constructor for class. Private to prevent having multiple instances.
   *
   * @param applicationContext the Android Application Context.
   */
  private ApplicationTraceManager(@NonNull final Context applicationContext) {
    this.dataStorage = TraceDataStorage.getInstance(applicationContext);
  }

  /**
   * Gets the instance of the ApplicationTraceManager.
   *
   * @param applicationContext the Android Application Context.
   * @return the instance.
   */
  @NonNull
  public static synchronized ApplicationTraceManager getInstance(
      @NonNull final Context applicationContext) {
    synchronized (applicationTraceManagerLock) {
      if (applicationTraceManager == null) {
        applicationTraceManager = new ApplicationTraceManager(applicationContext);
        TraceLog.d(LogMessageConstants.APPLICATION_TRACE_INITIALISED);
      }
      return applicationTraceManager;
    }
  }

  /**
   * Resets the state of the ApplicationTraceManager.
   */
  public static synchronized void reset() {
    synchronized (applicationTraceManagerLock) {
      if (applicationTraceManager == null) {
        return;
      }

      applicationTraceManager.stopTrace();
      applicationTraceManager = null;
    }
  }

  @Override
  public synchronized void startTrace() {
    synchronized (activeTraceLock) {
      if (activeTrace != null) {
        stopTrace();
      }
      activeTrace = new Trace();
      TraceLog.d(LogMessageConstants.APPLICATION_TRACE_STARTED);
    }
  }

  /**
   * Creates an ID for a {@link Span}.
   *
   * @return the String value of the ID of the Span.
   */
  @NonNull
  private String createSpanId() {
    return UniqueIdGenerator.makeSpanId();
  }

  @NonNull
  @Override
  public String createSpanId(final boolean isRoot) {
    final String spanId = createSpanId();

    if (isRoot) {
      this.rootSpanId = spanId;
    }

    return spanId;
  }

  @Nullable
  @Override
  public Trace getActiveTrace() {
    return activeTrace;
  }

  @Override
  public void addSpanToActiveTrace(@NonNull final Span span) {
    synchronized (activeTraceLock) {
      if (activeTrace == null) {
        startTrace();
      }
      final Span updatedSpan = span.toBuilder()
                                   .setTraceId(ByteString.copyFrom(activeTrace.getTraceId(),
                                       Charset.defaultCharset()))
                                   .build();
      activeTrace.addSpan(updatedSpan);
    }
  }

  @Override
  public synchronized void stopTrace() {
    synchronized (activeTraceLock) {
      if (activeTrace == null) {
        return;
      }
      final Trace traceToSave = activeTrace;
      activeTrace = null;
      rootSpanId = null;
      Executors.newSingleThreadExecutor().submit(() -> dataStorage.saveTraces(traceToSave));
      TraceLog.d(LogMessageConstants.APPLICATION_TRACE_STOPPED);
    }
  }

  @Nullable
  @Override
  public String getRootSpanId() {
    return rootSpanId;
  }

  @Override
  public boolean isInitialised() {
    return applicationTraceManager != null;
  }
}
