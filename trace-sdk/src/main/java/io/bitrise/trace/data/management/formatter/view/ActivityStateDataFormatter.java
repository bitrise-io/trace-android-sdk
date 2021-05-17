package io.bitrise.trace.data.management.formatter.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import io.bitrise.trace.data.collector.DataSourceType;
import io.bitrise.trace.data.dto.ActivityData;
import io.bitrise.trace.data.dto.ActivityState;
import io.bitrise.trace.data.dto.Data;
import io.bitrise.trace.data.dto.FormattedData;
import io.bitrise.trace.data.management.Formatter;
import io.bitrise.trace.data.management.formatter.DataFormatter;
import io.bitrise.trace.utils.TraceClock;
import io.bitrise.trace.utils.UniqueIdGenerator;
import io.opencensus.proto.trace.v1.Span;
import io.opencensus.proto.trace.v1.TruncatableString;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * {@link Formatter} implementation, to handle formatting for {@link DataSourceType#ACTIVITY_STATE}.
 */
public class ActivityStateDataFormatter extends DataFormatter {

  /**
   * Create a Span representing an {@link ActivityState}.
   *
   * @param name   the activity name.
   * @param start  the milliseconds when the activity was started.
   * @param end    the milliseconds when the activity was ended.
   * @param spanId from {@link UniqueIdGenerator#makeSpanId()}.
   * @return the Span.
   */
  @NonNull
  public static Span createActivityViewSpan(@NonNull final String name, final long start,
                                            final long end,
                                            @NonNull final String spanId) {
    final Timestamp startTimestamp = TraceClock.createTimestamp(start);
    final Timestamp endTimestamp = TraceClock.createTimestamp(end);
    return Span.newBuilder()
               .setStartTime(startTimestamp)
               .setEndTime(endTimestamp)
               .setName(TruncatableString.newBuilder()
                                         .setValue(name).build())
               .setSpanId(ByteString.copyFrom(spanId, Charset.defaultCharset()))
               .setKind(Span.SpanKind.CLIENT)
               .build();
  }

  @NonNull
  @Override
  public FormattedData[] formatData(@NonNull final Data data) {
    final Object content = data.getContent();
    if (!(content instanceof ActivityData)) {
      return new FormattedData[] {};
    }
    final ActivityData activityData = (ActivityData) content;
    final Map<ActivityState, Long> activityStates = activityData.getStateMap();
    final Long start = findStart(activityStates);
    final Long end = findEnd(activityStates);
    final String name = activityData.getName();

    if (start == null
        || end == null
        || name == null) {
      return new FormattedData[] {};
    }

    final FormattedData formattedData = new FormattedData(
        createActivityViewSpan(name, start, end, activityData.getSpanId()));
    return new FormattedData[] {formattedData};
  }

  /**
   * Finds the start timestamp (either CREATED or STARTED) from a map of activity states.
   * When the Activity is restarted it is not created again, only onStart is called. This way,
   * for that given trace there is no CREATED timestamp.
   *
   * @param activityStates - the list of activity states.
   * @return - the timestamp (in milliseconds) when the activity was started, or null if a
   *     start cannot be found.
   */
  @Nullable
  private Long findStart(Map<ActivityState, Long> activityStates) {
    if (activityStates.get(ActivityState.CREATED) != null) {
      return activityStates.get(ActivityState.CREATED);
    } else {
      return activityStates.get(ActivityState.STARTED);
    }
  }

  /**
   * Finds the end timestamp STOPPED or PAUSED from a map of activity states.
   * PAUSED is used if the activity is killed then STOPPED will not be called, but it might be
   * less accurate. Note: We do not currently collect the PAUSED state in the collector.
   *
   * @param activityStates - the list of activity states.
   * @return - the timestamp (in milliseconds) when the activity was ended, or null if an end cannot
   *     be found.
   */
  @Nullable
  private Long findEnd(Map<ActivityState, Long> activityStates) {
    if (activityStates.get(ActivityState.STOPPED) != null) {
      return activityStates.get(ActivityState.STOPPED);
    } else {
      return activityStates.get(ActivityState.PAUSED);
    }
  }
}
