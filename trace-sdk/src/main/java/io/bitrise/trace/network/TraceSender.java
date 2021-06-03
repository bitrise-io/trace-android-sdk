package io.bitrise.trace.network;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import com.google.common.util.concurrent.SettableFuture;
import io.bitrise.trace.data.trace.Trace;
import io.bitrise.trace.data.trace.TraceUtils;
import io.bitrise.trace.utils.TraceException;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.resource.v1.Resource;
import io.opencensus.proto.trace.v1.Span;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import retrofit2.Response;

/**
 * Responsible for sending {@link Trace}s to the server.
 */
public class TraceSender extends DataSender {

  /**
   * The List of Traces that will be sent.
   */
  @Nullable
  private List<Trace> traceList;

  @Override
  @SuppressLint("infer")
  public Future<Result> send(@Nullable final JobParameters params) {
    TraceLog.i(LogMessageConstants.TRACE_SENDING);
    setResultSettableFuture(SettableFuture.create());
    final SettableFuture<Result> settableFuture = getResultSettableFuture();
    getExecutor().execute(() -> {
      try {
        if (isStopped()) {
          settableFuture.set(Result.FAILURE);
          return;
        }

        final TraceRequest traceRequest = getNetworkRequest();
        if (!validateNetworkRequest(traceRequest)) {
          settableFuture.set(Result.FAILURE);
          return;
        }

        final Response<Void> response = getNetworkCommunicator().sendTraces(traceRequest).execute();
        if (response.isSuccessful()) {
          onSuccess();
        } else {
          TraceLog.w(
              new TraceException.TraceSenderFailedException(response.code(), response.message()));
          settableFuture.set(Result.FAILURE);
        }
      } catch (final IOException e) {
        TraceLog.w(e);
        settableFuture.setException(e);
      } catch (final IllegalStateException e) {
        TraceLog.w(e);
        if (!isStopped()) {
          throw e;
        }
      } finally {
        onSendingFinished(params, isRescheduleNeeded());
      }
    });
    return settableFuture;
  }

  @Override
  @SuppressLint("infer")
  public void onSuccess() {
    TraceLog.i(LogMessageConstants.TRACE_SENT_SUCCESSFULLY);
    getExecutor().execute(() -> {
      if (!getTraceList().isEmpty()) {
        getDataStorage().deleteTraces(getTraceList());
        removeResources(getTraceList().get(0).getSessionId());
      }

      getResultSettableFuture().set(Result.SUCCESS);
    });
  }

  @Nullable
  @Override
  @WorkerThread
  TraceRequest getNetworkRequest() {
    setTraceList(getDataStorage().getFirstTraceGroup());
    if (getTraceList().isEmpty()) {
      return null;
    }
    final Resource resource = getSessionResources(getTraceList().get(0).getSessionId());

    final List<Trace> emptyTraces = findEmptyTraces(getTraceList());
    removeEmptyTracesFromTraceList(emptyTraces);
    removeEmptyTracesFromDataStorage(emptyTraces);

    final List<Span> spans = TraceUtils.getSpans(getTraceList());
    if (spans.isEmpty()) {
      return null;
    }
    return new TraceRequest(resource, spans);
  }

  /**
   * Returns an array of {@link Trace}s that do not have any {@link Span}s to be later removed
   * from the {@link #traceList}.
   */
  @VisibleForTesting
  @NonNull
  List<Trace> findEmptyTraces(@Nullable final List<Trace> traceList) {
    if (traceList == null) {
      return new ArrayList<>();
    }

    final List<Trace> emptyTraces = new ArrayList<>();

    for (@NonNull final Trace trace : traceList) {
      if (trace.getSpanList().isEmpty()) {
        emptyTraces.add(trace);
        TraceLog.w(
            new TraceException.EmptyTraceException(trace.getTraceId()));
      }
    }

    return emptyTraces;
  }

  /**
   * Removes Traces without spans from the dataStore.
   *
   * @param emptyTraces the list of traces to be removed from the dataStorage.
   */
  private void removeEmptyTracesFromDataStorage(@NonNull final List<Trace> emptyTraces) {
    if (emptyTraces.size() > 0) {
      Executors.newSingleThreadExecutor().execute(() -> getDataStorage().deleteTraces(emptyTraces));
    }
  }

  @Override
  public boolean onStartJob(@Nullable final JobParameters params) {
    send(params);
    return true;
  }

  @Override
  @WorkerThread
  boolean hasData() {
    return getDataStorage().getAllTraces().size() > 0;
  }

  @VisibleForTesting
  @NonNull
  List<Trace> getTraceList() {
    if (traceList == null) {
      traceList = new ArrayList<>();
    }
    return traceList;
  }

  @VisibleForTesting
  void setTraceList(@NonNull final List<Trace> traceList) {
    this.traceList = traceList;
  }

  private void removeEmptyTracesFromTraceList(@NonNull final List<Trace> emptyTraces) {
    if (traceList != null) {
      traceList.removeAll(emptyTraces);
    }
  }
}
