package io.bitrise.trace.data.management.formatter.network;

import androidx.annotation.NonNull;

import com.google.protobuf.ByteString;

import java.nio.charset.Charset;

import io.bitrise.trace.data.collector.DataListener;
import io.bitrise.trace.data.collector.network.okhttp.OkHttpDataListener;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.DataValues;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.dto.NetworkData;
import io.bitrise.trace.data.management.formatter.DataFormatter;
import io.bitrise.trace.utils.TraceClock;
import io.opencensus.proto.trace.v1.AttributeValue;
import io.opencensus.proto.trace.v1.Span;
import io.opencensus.proto.trace.v1.TruncatableString;

import static io.bitrise.trace.data.dto.DataValues.http;
import static io.bitrise.trace.data.dto.DataValues.method;
import static io.bitrise.trace.data.dto.DataValues.status_code;
import static io.bitrise.trace.data.dto.DataValues.url;
import static io.bitrise.trace.test.TraceTestProvider.getTruncatableString;

/**
 * Formatter for networking related {@link DataListener} implementations, such as {@link OkHttpDataListener}.
 */
public class NetworkDataFormatter extends DataFormatter {

    @NonNull
    @Override
    public FormattedData[] formatData(@NonNull final Data data) {
        final Object contentObject = data.getContent();
        if (!(contentObject instanceof NetworkData)) {
            return new FormattedData[]{};
        }

        final NetworkData networkData = (NetworkData) contentObject;
        return toFormattedDataArray(getNetworkCallSpan(networkData));
    }

    /**
     * Creates a Span from the given {@link NetworkData}.
     *
     * @param networkData the given {@link NetworkData}.
     * @return the created Span.
     */
    @NonNull
    private Span getNetworkCallSpan(@NonNull final NetworkData networkData) {
        return createNetworkSpan(networkData, networkData.getUrl());
    }

    /**
     * Create a Span representing a {@link NetworkData}.
     *
     * @param networkData the given network data.
     * @return the span.
     */
    @NonNull
    public static Span createNetworkSpan(@NonNull final NetworkData networkData,
                                         @NonNull final String name) {
        return Span.newBuilder()
                .setStartTime(TraceClock.createTimestamp(networkData.getStart()))
                .setEndTime(TraceClock.createTimestamp(networkData.getEnd()))
                .setName(TruncatableString.newBuilder().setValue(name).build())
                .setSpanId(ByteString.copyFrom(networkData.getSpanId(), Charset.defaultCharset()))
                .setParentSpanId(ByteString.copyFrom(networkData.getParentSpanId(), Charset.defaultCharset()))
                .setAttributes(createAttributes(networkData))
                .setKind(Span.SpanKind.CLIENT)
                .build();
    }

    /**
     * Creates a {@link Span.Attributes} object for use in the {@link Span}.
     * @param networkData the network data to represent.
     * @return the attributes object.
     */
    @NonNull
    private static Span.Attributes createAttributes(@NonNull final NetworkData networkData) {
        return Span.Attributes.newBuilder()
                .putAttributeMap(
                        DataValues.getName(http, method),
                        createAttributeStringValue(networkData.getMethod()))
                .putAttributeMap(
                        DataValues.getName(http, url),
                        createAttributeStringValue(networkData.getUrl()))
                .putAttributeMap(
                        DataValues.getName(http, status_code),
                        createAttributeStringValue(Integer.toString(networkData.getStatusCode())))
                .build();
    }

    /**
     * Creates an {@link AttributeValue} from a {@link @String} value.
     * @param value the String value to represent.
     * @return the AttributeValue holding the String value.
     */
    @NonNull
    private static AttributeValue createAttributeStringValue(final String value) {
        return AttributeValue.newBuilder()
                .setStringValue(getTruncatableString(value))
                .build();
    }
}
