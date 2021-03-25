package io.bitrise.trace.network;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.common.util.concurrent.SettableFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import io.bitrise.trace.data.metric.MetricEntity;
import io.bitrise.trace.data.metric.MetricUtils;
import io.bitrise.trace.utils.TraceException;
import io.bitrise.trace.utils.log.LogMessageConstants;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.metrics.v1.Metric;
import io.opencensus.proto.resource.v1.Resource;
import retrofit2.Response;

/**
 * Responsible for sending {@link Metric}s to the server.
 */
public class MetricSender extends DataSender {

    /**
     * The List of Metrics that will be sent.
     */
    @Nullable
    private List<MetricEntity> metricEntityList;

    @Override
    @SuppressLint("infer")
    public Future<Result> send(@Nullable final JobParameters params) {
        TraceLog.i(LogMessageConstants.METRIC_SENDING);
        setResultSettableFuture(SettableFuture.create());
        final SettableFuture<Result> settableFuture = getResultSettableFuture();
        getExecutor().execute(() -> {
            try {
                if (isStopped()) {
                    settableFuture.set(Result.FAILURE);
                    onSendingFinished(params, false);
                    return;
                }

                final MetricRequest metricRequest = getNetworkRequest();
                if (!validateTraceRequest(metricRequest)) {
                    settableFuture.set(Result.FAILURE);
                    onSendingFinished(params, isRescheduleNeeded());
                    return;
                }

                final Response<Void> response = getNetworkCommunicator().sendMetrics(metricRequest).execute();
                if (response.isSuccessful()) {
                    onSuccess();
                } else {
                    TraceLog.w(
                            new TraceException.MetricSenderFailedException(response.code(), response.message()));
                    settableFuture.set(Result.FAILURE);
                }
            } catch (final IOException e) {
                TraceLog.w(e);
                getResultSettableFuture().setException(e);
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
        TraceLog.i(LogMessageConstants.METRIC_SENT_SUCCESSFULLY);
        getExecutor().execute(() -> {
            if (!getMetricEntityList().isEmpty()) {
                getDataStorage().deleteMetrics(getMetricEntityList());
                removeResources(getMetricEntityList().get(0).getSessionId());
            }

            getResultSettableFuture().set(Result.SUCCESS);
        });
    }

    @Override
    @Nullable
    @WorkerThread
    MetricRequest getNetworkRequest() {
        setMetricEntityList(getDataStorage().getFirstMetricGroup());
        if (getMetricEntityList().isEmpty()) {
            return null;
        }

        final List<Metric> metricList = MetricUtils.toMetricList(getMetricEntityList());
        final List<Metric> batchedMetricList = MetricUtils.batchMetrics(metricList.toArray(new Metric[]{}));
        final Resource resource = getSessionResources(getMetricEntityList().get(0).getSessionId());
        return new MetricRequest(resource, batchedMetricList);
    }

    @Override
    @WorkerThread
    boolean hasData() {
        return getDataStorage().getAllMetrics().size() > 0;
    }

    @Override
    public boolean onStartJob(@Nullable final JobParameters params) {
        send(params);
        return true;
    }

    @NonNull
    private List<MetricEntity> getMetricEntityList() {
        if (metricEntityList == null) {
            metricEntityList = new ArrayList<>();
        }
        return metricEntityList;
    }

    private void setMetricEntityList(@NonNull List<MetricEntity> metricEntityList) {
        this.metricEntityList = metricEntityList;
    }
}
