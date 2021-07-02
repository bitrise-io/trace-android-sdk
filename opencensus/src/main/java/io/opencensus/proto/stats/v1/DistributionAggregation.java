// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/stats/v1/stats.proto

package io.opencensus.proto.stats.v1;

/**
 * Protobuf type {@code opencensus.proto.stats.v1.DistributionAggregation}
 */
public  final class DistributionAggregation extends
    com.google.protobuf.GeneratedMessageLite<
        DistributionAggregation, DistributionAggregation.Builder> implements
    // @@protoc_insertion_point(message_implements:opencensus.proto.stats.v1.DistributionAggregation)
    DistributionAggregationOrBuilder {
  private DistributionAggregation() {
    bucketBounds_ = emptyDoubleList();
  }
  public static final int BUCKET_BOUNDS_FIELD_NUMBER = 1;
  private com.google.protobuf.Internal.DoubleList bucketBounds_;
  /**
   * <pre>
   * A Distribution may optionally contain a histogram of the values in the
   * population. The bucket boundaries for that histogram are described by
   * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
   * buckets. The boundaries for bucket index i are:
   * (-infinity, bucket_bounds[i]) for i == 0
   * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
   * [bucket_bounds[i-1], +infinity) for i == N-1
   * i.e. an underflow bucket (number 0), zero or more finite buckets (1
   * through N - 2, and an overflow bucket (N - 1), with inclusive lower
   * bounds and exclusive upper bounds.
   * If `bucket_bounds` has no elements (zero size), then there is no
   * histogram associated with the Distribution. If `bucket_bounds` has only
   * one element, there are no finite buckets, and that single element is the
   * common boundary of the overflow and underflow buckets. The values must
   * be monotonically increasing.
   * </pre>
   *
   * <code>repeated double bucket_bounds = 1;</code>
   * @return A list containing the bucketBounds.
   */
  @java.lang.Override
  public java.util.List<java.lang.Double>
      getBucketBoundsList() {
    return bucketBounds_;
  }
  /**
   * <pre>
   * A Distribution may optionally contain a histogram of the values in the
   * population. The bucket boundaries for that histogram are described by
   * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
   * buckets. The boundaries for bucket index i are:
   * (-infinity, bucket_bounds[i]) for i == 0
   * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
   * [bucket_bounds[i-1], +infinity) for i == N-1
   * i.e. an underflow bucket (number 0), zero or more finite buckets (1
   * through N - 2, and an overflow bucket (N - 1), with inclusive lower
   * bounds and exclusive upper bounds.
   * If `bucket_bounds` has no elements (zero size), then there is no
   * histogram associated with the Distribution. If `bucket_bounds` has only
   * one element, there are no finite buckets, and that single element is the
   * common boundary of the overflow and underflow buckets. The values must
   * be monotonically increasing.
   * </pre>
   *
   * <code>repeated double bucket_bounds = 1;</code>
   * @return The count of bucketBounds.
   */
  @java.lang.Override
  public int getBucketBoundsCount() {
    return bucketBounds_.size();
  }
  /**
   * <pre>
   * A Distribution may optionally contain a histogram of the values in the
   * population. The bucket boundaries for that histogram are described by
   * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
   * buckets. The boundaries for bucket index i are:
   * (-infinity, bucket_bounds[i]) for i == 0
   * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
   * [bucket_bounds[i-1], +infinity) for i == N-1
   * i.e. an underflow bucket (number 0), zero or more finite buckets (1
   * through N - 2, and an overflow bucket (N - 1), with inclusive lower
   * bounds and exclusive upper bounds.
   * If `bucket_bounds` has no elements (zero size), then there is no
   * histogram associated with the Distribution. If `bucket_bounds` has only
   * one element, there are no finite buckets, and that single element is the
   * common boundary of the overflow and underflow buckets. The values must
   * be monotonically increasing.
   * </pre>
   *
   * <code>repeated double bucket_bounds = 1;</code>
   * @param index The index of the element to return.
   * @return The bucketBounds at the given index.
   */
  @java.lang.Override
  public double getBucketBounds(int index) {
    return bucketBounds_.getDouble(index);
  }
  private int bucketBoundsMemoizedSerializedSize = -1;
  private void ensureBucketBoundsIsMutable() {
    com.google.protobuf.Internal.DoubleList tmp = bucketBounds_;
    if (!tmp.isModifiable()) {
      bucketBounds_ =
          com.google.protobuf.GeneratedMessageLite.mutableCopy(tmp);
     }
  }
  /**
   * <pre>
   * A Distribution may optionally contain a histogram of the values in the
   * population. The bucket boundaries for that histogram are described by
   * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
   * buckets. The boundaries for bucket index i are:
   * (-infinity, bucket_bounds[i]) for i == 0
   * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
   * [bucket_bounds[i-1], +infinity) for i == N-1
   * i.e. an underflow bucket (number 0), zero or more finite buckets (1
   * through N - 2, and an overflow bucket (N - 1), with inclusive lower
   * bounds and exclusive upper bounds.
   * If `bucket_bounds` has no elements (zero size), then there is no
   * histogram associated with the Distribution. If `bucket_bounds` has only
   * one element, there are no finite buckets, and that single element is the
   * common boundary of the overflow and underflow buckets. The values must
   * be monotonically increasing.
   * </pre>
   *
   * <code>repeated double bucket_bounds = 1;</code>
   * @param index The index to set the value at.
   * @param value The bucketBounds to set.
   */
  private void setBucketBounds(
      int index, double value) {
    ensureBucketBoundsIsMutable();
    bucketBounds_.setDouble(index, value);
  }
  /**
   * <pre>
   * A Distribution may optionally contain a histogram of the values in the
   * population. The bucket boundaries for that histogram are described by
   * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
   * buckets. The boundaries for bucket index i are:
   * (-infinity, bucket_bounds[i]) for i == 0
   * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
   * [bucket_bounds[i-1], +infinity) for i == N-1
   * i.e. an underflow bucket (number 0), zero or more finite buckets (1
   * through N - 2, and an overflow bucket (N - 1), with inclusive lower
   * bounds and exclusive upper bounds.
   * If `bucket_bounds` has no elements (zero size), then there is no
   * histogram associated with the Distribution. If `bucket_bounds` has only
   * one element, there are no finite buckets, and that single element is the
   * common boundary of the overflow and underflow buckets. The values must
   * be monotonically increasing.
   * </pre>
   *
   * <code>repeated double bucket_bounds = 1;</code>
   * @param value The bucketBounds to add.
   */
  private void addBucketBounds(double value) {
    ensureBucketBoundsIsMutable();
    bucketBounds_.addDouble(value);
  }
  /**
   * <pre>
   * A Distribution may optionally contain a histogram of the values in the
   * population. The bucket boundaries for that histogram are described by
   * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
   * buckets. The boundaries for bucket index i are:
   * (-infinity, bucket_bounds[i]) for i == 0
   * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
   * [bucket_bounds[i-1], +infinity) for i == N-1
   * i.e. an underflow bucket (number 0), zero or more finite buckets (1
   * through N - 2, and an overflow bucket (N - 1), with inclusive lower
   * bounds and exclusive upper bounds.
   * If `bucket_bounds` has no elements (zero size), then there is no
   * histogram associated with the Distribution. If `bucket_bounds` has only
   * one element, there are no finite buckets, and that single element is the
   * common boundary of the overflow and underflow buckets. The values must
   * be monotonically increasing.
   * </pre>
   *
   * <code>repeated double bucket_bounds = 1;</code>
   * @param values The bucketBounds to add.
   */
  private void addAllBucketBounds(
      java.lang.Iterable<? extends java.lang.Double> values) {
    ensureBucketBoundsIsMutable();
    com.google.protobuf.AbstractMessageLite.addAll(
        values, bucketBounds_);
  }
  /**
   * <pre>
   * A Distribution may optionally contain a histogram of the values in the
   * population. The bucket boundaries for that histogram are described by
   * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
   * buckets. The boundaries for bucket index i are:
   * (-infinity, bucket_bounds[i]) for i == 0
   * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
   * [bucket_bounds[i-1], +infinity) for i == N-1
   * i.e. an underflow bucket (number 0), zero or more finite buckets (1
   * through N - 2, and an overflow bucket (N - 1), with inclusive lower
   * bounds and exclusive upper bounds.
   * If `bucket_bounds` has no elements (zero size), then there is no
   * histogram associated with the Distribution. If `bucket_bounds` has only
   * one element, there are no finite buckets, and that single element is the
   * common boundary of the overflow and underflow buckets. The values must
   * be monotonically increasing.
   * </pre>
   *
   * <code>repeated double bucket_bounds = 1;</code>
   */
  private void clearBucketBounds() {
    bucketBounds_ = emptyDoubleList();
  }

  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.stats.v1.DistributionAggregation parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(io.opencensus.proto.stats.v1.DistributionAggregation prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * Protobuf type {@code opencensus.proto.stats.v1.DistributionAggregation}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        io.opencensus.proto.stats.v1.DistributionAggregation, Builder> implements
      // @@protoc_insertion_point(builder_implements:opencensus.proto.stats.v1.DistributionAggregation)
      io.opencensus.proto.stats.v1.DistributionAggregationOrBuilder {
    // Construct using io.opencensus.proto.stats.v1.DistributionAggregation.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    /**
     * <pre>
     * A Distribution may optionally contain a histogram of the values in the
     * population. The bucket boundaries for that histogram are described by
     * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
     * buckets. The boundaries for bucket index i are:
     * (-infinity, bucket_bounds[i]) for i == 0
     * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
     * [bucket_bounds[i-1], +infinity) for i == N-1
     * i.e. an underflow bucket (number 0), zero or more finite buckets (1
     * through N - 2, and an overflow bucket (N - 1), with inclusive lower
     * bounds and exclusive upper bounds.
     * If `bucket_bounds` has no elements (zero size), then there is no
     * histogram associated with the Distribution. If `bucket_bounds` has only
     * one element, there are no finite buckets, and that single element is the
     * common boundary of the overflow and underflow buckets. The values must
     * be monotonically increasing.
     * </pre>
     *
     * <code>repeated double bucket_bounds = 1;</code>
     * @return A list containing the bucketBounds.
     */
    @java.lang.Override
    public java.util.List<java.lang.Double>
        getBucketBoundsList() {
      return java.util.Collections.unmodifiableList(
          instance.getBucketBoundsList());
    }
    /**
     * <pre>
     * A Distribution may optionally contain a histogram of the values in the
     * population. The bucket boundaries for that histogram are described by
     * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
     * buckets. The boundaries for bucket index i are:
     * (-infinity, bucket_bounds[i]) for i == 0
     * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
     * [bucket_bounds[i-1], +infinity) for i == N-1
     * i.e. an underflow bucket (number 0), zero or more finite buckets (1
     * through N - 2, and an overflow bucket (N - 1), with inclusive lower
     * bounds and exclusive upper bounds.
     * If `bucket_bounds` has no elements (zero size), then there is no
     * histogram associated with the Distribution. If `bucket_bounds` has only
     * one element, there are no finite buckets, and that single element is the
     * common boundary of the overflow and underflow buckets. The values must
     * be monotonically increasing.
     * </pre>
     *
     * <code>repeated double bucket_bounds = 1;</code>
     * @return The count of bucketBounds.
     */
    @java.lang.Override
    public int getBucketBoundsCount() {
      return instance.getBucketBoundsCount();
    }
    /**
     * <pre>
     * A Distribution may optionally contain a histogram of the values in the
     * population. The bucket boundaries for that histogram are described by
     * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
     * buckets. The boundaries for bucket index i are:
     * (-infinity, bucket_bounds[i]) for i == 0
     * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
     * [bucket_bounds[i-1], +infinity) for i == N-1
     * i.e. an underflow bucket (number 0), zero or more finite buckets (1
     * through N - 2, and an overflow bucket (N - 1), with inclusive lower
     * bounds and exclusive upper bounds.
     * If `bucket_bounds` has no elements (zero size), then there is no
     * histogram associated with the Distribution. If `bucket_bounds` has only
     * one element, there are no finite buckets, and that single element is the
     * common boundary of the overflow and underflow buckets. The values must
     * be monotonically increasing.
     * </pre>
     *
     * <code>repeated double bucket_bounds = 1;</code>
     * @param index The index of the element to return.
     * @return The bucketBounds at the given index.
     */
    @java.lang.Override
    public double getBucketBounds(int index) {
      return instance.getBucketBounds(index);
    }
    /**
     * <pre>
     * A Distribution may optionally contain a histogram of the values in the
     * population. The bucket boundaries for that histogram are described by
     * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
     * buckets. The boundaries for bucket index i are:
     * (-infinity, bucket_bounds[i]) for i == 0
     * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
     * [bucket_bounds[i-1], +infinity) for i == N-1
     * i.e. an underflow bucket (number 0), zero or more finite buckets (1
     * through N - 2, and an overflow bucket (N - 1), with inclusive lower
     * bounds and exclusive upper bounds.
     * If `bucket_bounds` has no elements (zero size), then there is no
     * histogram associated with the Distribution. If `bucket_bounds` has only
     * one element, there are no finite buckets, and that single element is the
     * common boundary of the overflow and underflow buckets. The values must
     * be monotonically increasing.
     * </pre>
     *
     * <code>repeated double bucket_bounds = 1;</code>
     * @param value The bucketBounds to set.
     * @return This builder for chaining.
     */
    public Builder setBucketBounds(
        int index, double value) {
      copyOnWrite();
      instance.setBucketBounds(index, value);
      return this;
    }
    /**
     * <pre>
     * A Distribution may optionally contain a histogram of the values in the
     * population. The bucket boundaries for that histogram are described by
     * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
     * buckets. The boundaries for bucket index i are:
     * (-infinity, bucket_bounds[i]) for i == 0
     * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
     * [bucket_bounds[i-1], +infinity) for i == N-1
     * i.e. an underflow bucket (number 0), zero or more finite buckets (1
     * through N - 2, and an overflow bucket (N - 1), with inclusive lower
     * bounds and exclusive upper bounds.
     * If `bucket_bounds` has no elements (zero size), then there is no
     * histogram associated with the Distribution. If `bucket_bounds` has only
     * one element, there are no finite buckets, and that single element is the
     * common boundary of the overflow and underflow buckets. The values must
     * be monotonically increasing.
     * </pre>
     *
     * <code>repeated double bucket_bounds = 1;</code>
     * @param value The bucketBounds to add.
     * @return This builder for chaining.
     */
    public Builder addBucketBounds(double value) {
      copyOnWrite();
      instance.addBucketBounds(value);
      return this;
    }
    /**
     * <pre>
     * A Distribution may optionally contain a histogram of the values in the
     * population. The bucket boundaries for that histogram are described by
     * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
     * buckets. The boundaries for bucket index i are:
     * (-infinity, bucket_bounds[i]) for i == 0
     * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
     * [bucket_bounds[i-1], +infinity) for i == N-1
     * i.e. an underflow bucket (number 0), zero or more finite buckets (1
     * through N - 2, and an overflow bucket (N - 1), with inclusive lower
     * bounds and exclusive upper bounds.
     * If `bucket_bounds` has no elements (zero size), then there is no
     * histogram associated with the Distribution. If `bucket_bounds` has only
     * one element, there are no finite buckets, and that single element is the
     * common boundary of the overflow and underflow buckets. The values must
     * be monotonically increasing.
     * </pre>
     *
     * <code>repeated double bucket_bounds = 1;</code>
     * @param values The bucketBounds to add.
     * @return This builder for chaining.
     */
    public Builder addAllBucketBounds(
        java.lang.Iterable<? extends java.lang.Double> values) {
      copyOnWrite();
      instance.addAllBucketBounds(values);
      return this;
    }
    /**
     * <pre>
     * A Distribution may optionally contain a histogram of the values in the
     * population. The bucket boundaries for that histogram are described by
     * `bucket_bounds`. This defines `size(bucket_bounds) + 1` (= N)
     * buckets. The boundaries for bucket index i are:
     * (-infinity, bucket_bounds[i]) for i == 0
     * [bucket_bounds[i-1], bucket_bounds[i]) for 0 &lt; i &lt; N-2
     * [bucket_bounds[i-1], +infinity) for i == N-1
     * i.e. an underflow bucket (number 0), zero or more finite buckets (1
     * through N - 2, and an overflow bucket (N - 1), with inclusive lower
     * bounds and exclusive upper bounds.
     * If `bucket_bounds` has no elements (zero size), then there is no
     * histogram associated with the Distribution. If `bucket_bounds` has only
     * one element, there are no finite buckets, and that single element is the
     * common boundary of the overflow and underflow buckets. The values must
     * be monotonically increasing.
     * </pre>
     *
     * <code>repeated double bucket_bounds = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearBucketBounds() {
      copyOnWrite();
      instance.clearBucketBounds();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:opencensus.proto.stats.v1.DistributionAggregation)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new io.opencensus.proto.stats.v1.DistributionAggregation();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "bucketBounds_",
          };
          java.lang.String info =
              "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001#";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<io.opencensus.proto.stats.v1.DistributionAggregation> parser = PARSER;
        if (parser == null) {
          synchronized (io.opencensus.proto.stats.v1.DistributionAggregation.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<io.opencensus.proto.stats.v1.DistributionAggregation>(
                      DEFAULT_INSTANCE);
              PARSER = parser;
            }
          }
        }
        return parser;
    }
    case GET_MEMOIZED_IS_INITIALIZED: {
      return (byte) 1;
    }
    case SET_MEMOIZED_IS_INITIALIZED: {
      return null;
    }
    }
    throw new UnsupportedOperationException();
  }


  // @@protoc_insertion_point(class_scope:opencensus.proto.stats.v1.DistributionAggregation)
  private static final io.opencensus.proto.stats.v1.DistributionAggregation DEFAULT_INSTANCE;
  static {
    DistributionAggregation defaultInstance = new DistributionAggregation();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      DistributionAggregation.class, defaultInstance);
  }

  public static io.opencensus.proto.stats.v1.DistributionAggregation getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<DistributionAggregation> PARSER;

  public static com.google.protobuf.Parser<DistributionAggregation> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

