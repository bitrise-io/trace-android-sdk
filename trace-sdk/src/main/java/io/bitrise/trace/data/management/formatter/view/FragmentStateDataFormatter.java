package io.bitrise.trace.data.management.formatter.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.dto.FragmentData;
import io.bitrise.trace.data.dto.FragmentDataStateEntry;
import io.bitrise.trace.data.dto.FragmentState;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.management.formatter.DataFormatter;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.TraceException;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.bitrise.trace.utils.log.TraceLog;
import io.opencensus.proto.trace.v1.Span;
import io.opencensus.proto.trace.v1.TruncatableString;

/**
 * {@link Formatter} implementation, to handle formatting for {@link DataSourceType#FRAGMENT_STATE}.
 */
public class FragmentStateDataFormatter extends DataFormatter {

    /**
     * Indicates that there is no element in the given Collection that is valid for the given method call. Used for
     * {@link #getFirstValidPausedIndex}.
     */
    @VisibleForTesting
    static final int INVALID_INDEX = -1;

    /**
     * Filters a List of {@link FragmentDataStateEntry} by it's {@link FragmentState}, returns only the matching ones.
     *
     * @param fragmentDataStateEntries the given FragmentDataStateEntries to filter.
     * @param fragmentState            the given FragmentState to include.
     * @return the filtered List.
     */
    @VisibleForTesting
    @NonNull
    static List<FragmentDataStateEntry> findAllByState(@NonNull final List<FragmentDataStateEntry> fragmentDataStateEntries,
                                                       @NonNull final FragmentState fragmentState) {
        final List<FragmentDataStateEntry> matching = new ArrayList<>();
        for (@NonNull final FragmentDataStateEntry fragmentDataStateEntry : fragmentDataStateEntries) {
            if (fragmentState.equals(fragmentDataStateEntry.getFragmentState())) {
                matching.add(fragmentDataStateEntry);
            }
        }
        return matching;
    }

    @NonNull
    @Override
    public FormattedData[] formatData(@NonNull final Data data) {
        final Object contentObject = data.getContent();
        if (!(contentObject instanceof Map)) {
            return new FormattedData[]{};
        }
        @SuppressWarnings("unchecked") final Map<Integer, FragmentData> contentMap =
                (Map<Integer, FragmentData>) contentObject;

        final List<Span> spans = new ArrayList<>();
        for (@NonNull final FragmentData fragmentData : contentMap.values()) {
            spans.addAll(getFragmentSpans(fragmentData));
        }

        return toFormattedDataArray(spans);
    }

    /**
     * Gets the List of {@link Span}s from the given {@link FragmentData}.
     *
     * @param fragmentData the FragmentData for the given Fragment.
     * @return the created Spans.
     */
    @VisibleForTesting
    @NonNull
    static List<Span> getFragmentSpans(@NonNull final FragmentData fragmentData) {
        final List<FragmentDataStateEntry> states = fragmentData.getStates();
        final List<FragmentDataStateEntry> onViewCreatedEntries = findAllByState(states, FragmentState.VIEW_CREATED);
        List<FragmentDataStateEntry> pausedEntries = findAllByState(states, FragmentState.PAUSED);

        final String name = fragmentData.getName();
        if (!validateEntries(name, onViewCreatedEntries, pausedEntries)) {
            return Collections.emptyList();
        }
        pausedEntries = validatePausedEntries(name, onViewCreatedEntries, pausedEntries);

        return getFragmentSpans(fragmentData.getSpanId(), name, fragmentData.getParentSpanId(), onViewCreatedEntries,
                pausedEntries);
    }

    /**
     * Gets the List of {@link Span}s from the given {@link FragmentData}.
     *
     * @param spanId               the Span ID of the Fragment.
     * @param name                 the name of the Fragment.
     * @param parentSpanId         the Span ID of the parent.
     * @param onViewCreatedEntries the {@link FragmentDataStateEntry}s with {@link FragmentState#VIEW_CREATED}.
     * @param pausedEntries        the FragmentDataStateEntries with {@link FragmentState#PAUSED}.
     * @return the created Spans.
     */
    @VisibleForTesting
    @NonNull
    static List<Span> getFragmentSpans(@NonNull final String spanId,
                                       @NonNull final String name,
                                       @Nullable final String parentSpanId,
                                       @NonNull final List<FragmentDataStateEntry> onViewCreatedEntries,
                                       @NonNull final List<FragmentDataStateEntry> pausedEntries) {
        final int numberOfSpans = Math.min(onViewCreatedEntries.size(), pausedEntries.size());
        final List<Span> spans = new ArrayList<>();

        for (int i = 0; i < numberOfSpans; i++) {
            final Span span = createFragmentViewSpan(name, onViewCreatedEntries.get(i).getTimeStamp(),
                    pausedEntries.get(i).getTimeStamp(), spanId, parentSpanId);
            spans.add(span);
        }

        return spans;
    }

    /**
     * Validates the {@link FragmentDataStateEntry}s and logs warning messages if something was invalid.
     *
     * @param fragmentName         the name of the Fragment that created theFragmentDataStateEntries.
     * @param onViewCreatedEntries the FragmentDataStateEntries with the {@link FragmentState#VIEW_CREATED} state.
     * @param pausedEntries        the FragmentDataStateEntries with the {@link FragmentState#PAUSED} state.
     * @return {@code true} if {@link Span}s can be created FragmentDataStateEntries, {@code false} otherwise.
     */
    @VisibleForTesting
    static boolean validateEntries(@NonNull final String fragmentName,
                                   @NonNull final List<FragmentDataStateEntry> onViewCreatedEntries,
                                   @NonNull final List<FragmentDataStateEntry> pausedEntries) {
        final int onViewCreatedEntriesSize = onViewCreatedEntries.size();
        final int pausedEntriesSize = pausedEntries.size();

        if (onViewCreatedEntriesSize != pausedEntriesSize) {
            TraceLog.w(
                    new TraceException.FragmentStateDataIncompleteSpansException(onViewCreatedEntriesSize, pausedEntriesSize));
        }

        if (onViewCreatedEntriesSize == 0) {
            TraceLog.w(
                    new TraceException.FragmentStateDataNoOnViewCreatedException(fragmentName));
            return false;
        }

        if (pausedEntriesSize == 0) {
            TraceLog.w(
                    new TraceException.FragmentStateDataNoPausedSpansException(fragmentName));
            return false;
        }
        return true;
    }

    /**
     * Validates the {@link FragmentDataStateEntry}s with {@link FragmentState#PAUSED} state. If there is a
     * difference between the number of FragmentDataStateEntries with {@link FragmentState#VIEW_CREATED} and PAUSED
     * state this will filter out the unusable PAUSED states.
     *
     * @param onViewCreatedEntries FragmentDataStateEntries with VIEW_CREATED state.
     * @param pausedEntries        FragmentDataStateEntries with PAUSED state.
     * @return the updated List of FragmentDataStateEntries with PAUSED state.
     */
    @VisibleForTesting
    @NonNull
    static List<FragmentDataStateEntry> validatePausedEntries(
            @NonNull final String fragmentName,
            @NonNull final List<FragmentDataStateEntry> onViewCreatedEntries,
            @NonNull final List<FragmentDataStateEntry> pausedEntries) {
        sortFragmentDataStates(onViewCreatedEntries);
        sortFragmentDataStates(pausedEntries);

        final long firstOnViewCreatedTime = onViewCreatedEntries.get(0).getTimeStamp();
        final int firstValidPausedIndex = getFirstValidPausedIndex(firstOnViewCreatedTime, pausedEntries);
        if (firstValidPausedIndex < 0) {
            TraceLog.w(
                    new TraceException.FragmentStateDataNoValidSpansException(fragmentName));
            return Collections.emptyList();
        }

        return pausedEntries.subList(firstValidPausedIndex, pausedEntries.size());
    }

    /**
     * Sorts a List of {@link FragmentDataStateEntry}s with a {@link FragmentDataStateEntry.Comparator}.
     *
     * @param fragmentDataStateEntries the sorted entries.
     */
    @VisibleForTesting
    static void sortFragmentDataStates(@NonNull final List<FragmentDataStateEntry> fragmentDataStateEntries) {
        Collections.sort(fragmentDataStateEntries, new FragmentDataStateEntry.Comparator());
    }

    /**
     * Gets the index of the first valid {@link FragmentDataStateEntry}s with {@link FragmentState#PAUSED} state. It
     * should have a timestamp that is higher than the first {@link FragmentDataStateEntry}s with
     * {@link FragmentState#VIEW_CREATED} timestamp.
     *
     * @param firstOnViewCreatedTime the timestamp of the first FragmentDataStateEntry with VIEW_CREATED state.
     * @param pausedEntries          FragmentDataStateEntries with PAUSED state.
     * @return the index from the provided List.
     */
    @VisibleForTesting
    static int getFirstValidPausedIndex(final long firstOnViewCreatedTime,
                                        @NonNull final List<FragmentDataStateEntry> pausedEntries) {
        for (int i = 0; i < pausedEntries.size(); i++) {
            if (pausedEntries.get(i).getTimeStamp() > firstOnViewCreatedTime) {
                return i;
            }
            TraceLog.w(
                    new TraceException.FragmentStateNoViewPausedAfterCreatedTimestampException(pausedEntries.get(i).getTimeStamp()));
        }
        return INVALID_INDEX;
    }

    /**
     * Create a Span representing a {@link FragmentState}.
     * @param name the fragment name.
     * @param start the milliseconds when the activity was started.
     * @param end the milliseconds when the activity was ended.
     * @param spanId from {@link UniqueIdGenerator#makeSpanId()}.
     * @param parentSpanId the parent activity span id.
     * @return the Span.
     */
    @NonNull
    public static Span createFragmentViewSpan(@NonNull final String name, final long start, final long end,
                                              @NonNull final String spanId, @Nullable String parentSpanId) {
        final Timestamp startTimestamp = TraceClock.createTimestamp(start);
        final Timestamp endTimestamp = TraceClock.createTimestamp(end);
        final Span.Builder span = Span.newBuilder()
                .setStartTime(startTimestamp)
                .setEndTime(endTimestamp)
                .setName(TruncatableString.newBuilder()
                        .setValue(name).build())
                .setSpanId(ByteString.copyFrom(spanId, Charset.defaultCharset()))
                .setKind(Span.SpanKind.CLIENT);

        if (parentSpanId != null) {
            span.setParentSpanId(ByteString.copyFrom(parentSpanId, Charset.defaultCharset()));
        }

         return span.build();
    }
}
