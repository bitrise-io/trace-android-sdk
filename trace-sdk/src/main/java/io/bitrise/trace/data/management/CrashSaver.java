package io.bitrise.trace.data.management;

import androidx.annotation.Nullable;
import io.bitrise.trace.data.dto.CrashReport;
import io.bitrise.trace.data.management.formatter.crash.CrashDataFormatter;
import io.bitrise.trace.data.storage.DataStorage;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.network.CrashRequest;
import io.bitrise.trace.session.Session;
import io.bitrise.trace.utils.ByteStringConverter;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.resource.v1.Resource;
import io.opencensus.proto.trace.v1.Span;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;

/**
 * Object that handles saving crash reports.
 */
public class CrashSaver {

  /**
   * Validates if we have enough data to create a {@link CrashRequest} and then stores it in the
   * {@link DataStorage}.
   *
   * @param resource    the current sessions {@link Resource}.
   * @param session     the current {@link Session}.
   * @param activeTrace the currently active {@link Trace}.
   * @param crashReport the crash report created by the {@link CrashDataFormatter}
   * @param dataStorage access to the current {@link DataStorage}.
   */
  static void saveCrash(@Nullable final Resource resource,
                        @Nullable final Session session,
                        @Nullable final Trace activeTrace,
                        @Nonnull final CrashReport crashReport,
                        @Nonnull final DataStorage dataStorage) {

    if (session == null) {
      TraceLog.d("Crash Saver: active session was null.");
      return;
    }

    if (resource == null) {
      TraceLog.d("Crash Saver: session resources were null.");
      return;
    }

    if (activeTrace == null) {
      TraceLog.d("Crash Saver: active trace was null.");
      return;
    }

    final Span lastSpan = activeTrace.getLastActiveViewSpan();

    final CrashRequest request = new CrashRequest(
        resource,
        crashReport,
        TraceClock.getCurrentTimeMillis(),
        TimeZone.getDefault(),
        UniqueIdGenerator.makeCrashReportId(),
        activeTrace.getTraceId(),
        lastSpan == null ? "" : ByteStringConverter.toString(lastSpan.getSpanId())
    );

    final ExecutorService service = Executors.newFixedThreadPool(1);
    Future<?> future = service.submit(() -> {
      dataStorage.saveCrashRequest(request);
    });

    // because we're in a crash loop - we need to access the future to give it time to actually save
    // otherwise this process gets killed before the save actually happens.
    try {
      future.get();
    } catch (ExecutionException | InterruptedException e) {
      // nop
    }
  }
}
