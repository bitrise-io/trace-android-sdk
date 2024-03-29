// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/trace/v1/trace.proto

package io.opencensus.proto.trace.v1;

/**
 * <pre>
 * A string that might be shortened to a specified length.
 * </pre>
 *
 * Protobuf type {@code opencensus.proto.trace.v1.TruncatableString}
 */
public  final class TruncatableString extends
    com.google.protobuf.GeneratedMessageLite<
        TruncatableString, TruncatableString.Builder> implements
    // @@protoc_insertion_point(message_implements:opencensus.proto.trace.v1.TruncatableString)
    TruncatableStringOrBuilder {
  private TruncatableString() {
    value_ = "";
  }
  public static final int VALUE_FIELD_NUMBER = 1;
  private java.lang.String value_;
  /**
   * <pre>
   * The shortened string. For example, if the original string was 500 bytes long and
   * the limit of the string was 128 bytes, then this value contains the first 128
   * bytes of the 500-byte string. Note that truncation always happens on a
   * character boundary, to ensure that a truncated string is still valid UTF-8.
   * Because it may contain multi-byte characters, the size of the truncated string
   * may be less than the truncation limit.
   * </pre>
   *
   * <code>string value = 1;</code>
   * @return The value.
   */
  @java.lang.Override
  public java.lang.String getValue() {
    return value_;
  }
  /**
   * <pre>
   * The shortened string. For example, if the original string was 500 bytes long and
   * the limit of the string was 128 bytes, then this value contains the first 128
   * bytes of the 500-byte string. Note that truncation always happens on a
   * character boundary, to ensure that a truncated string is still valid UTF-8.
   * Because it may contain multi-byte characters, the size of the truncated string
   * may be less than the truncation limit.
   * </pre>
   *
   * <code>string value = 1;</code>
   * @return The bytes for value.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getValueBytes() {
    return com.google.protobuf.ByteString.copyFromUtf8(value_);
  }
  /**
   * <pre>
   * The shortened string. For example, if the original string was 500 bytes long and
   * the limit of the string was 128 bytes, then this value contains the first 128
   * bytes of the 500-byte string. Note that truncation always happens on a
   * character boundary, to ensure that a truncated string is still valid UTF-8.
   * Because it may contain multi-byte characters, the size of the truncated string
   * may be less than the truncation limit.
   * </pre>
   *
   * <code>string value = 1;</code>
   * @param value The value to set.
   */
  private void setValue(
      java.lang.String value) {
    value.getClass();
  
    value_ = value;
  }
  /**
   * <pre>
   * The shortened string. For example, if the original string was 500 bytes long and
   * the limit of the string was 128 bytes, then this value contains the first 128
   * bytes of the 500-byte string. Note that truncation always happens on a
   * character boundary, to ensure that a truncated string is still valid UTF-8.
   * Because it may contain multi-byte characters, the size of the truncated string
   * may be less than the truncation limit.
   * </pre>
   *
   * <code>string value = 1;</code>
   */
  private void clearValue() {
    
    value_ = getDefaultInstance().getValue();
  }
  /**
   * <pre>
   * The shortened string. For example, if the original string was 500 bytes long and
   * the limit of the string was 128 bytes, then this value contains the first 128
   * bytes of the 500-byte string. Note that truncation always happens on a
   * character boundary, to ensure that a truncated string is still valid UTF-8.
   * Because it may contain multi-byte characters, the size of the truncated string
   * may be less than the truncation limit.
   * </pre>
   *
   * <code>string value = 1;</code>
   * @param value The bytes for value to set.
   */
  private void setValueBytes(
      com.google.protobuf.ByteString value) {
    checkByteStringIsUtf8(value);
    value_ = value.toStringUtf8();
    
  }

  public static final int TRUNCATED_BYTE_COUNT_FIELD_NUMBER = 2;
  private int truncatedByteCount_;
  /**
   * <pre>
   * The number of bytes removed from the original string. If this
   * value is 0, then the string was not shortened.
   * </pre>
   *
   * <code>int32 truncated_byte_count = 2;</code>
   * @return The truncatedByteCount.
   */
  @java.lang.Override
  public int getTruncatedByteCount() {
    return truncatedByteCount_;
  }
  /**
   * <pre>
   * The number of bytes removed from the original string. If this
   * value is 0, then the string was not shortened.
   * </pre>
   *
   * <code>int32 truncated_byte_count = 2;</code>
   * @param value The truncatedByteCount to set.
   */
  private void setTruncatedByteCount(int value) {
    
    truncatedByteCount_ = value;
  }
  /**
   * <pre>
   * The number of bytes removed from the original string. If this
   * value is 0, then the string was not shortened.
   * </pre>
   *
   * <code>int32 truncated_byte_count = 2;</code>
   */
  private void clearTruncatedByteCount() {
    
    truncatedByteCount_ = 0;
  }

  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.trace.v1.TruncatableString parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(io.opencensus.proto.trace.v1.TruncatableString prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * <pre>
   * A string that might be shortened to a specified length.
   * </pre>
   *
   * Protobuf type {@code opencensus.proto.trace.v1.TruncatableString}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        io.opencensus.proto.trace.v1.TruncatableString, Builder> implements
      // @@protoc_insertion_point(builder_implements:opencensus.proto.trace.v1.TruncatableString)
      io.opencensus.proto.trace.v1.TruncatableStringOrBuilder {
    // Construct using io.opencensus.proto.trace.v1.TruncatableString.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    /**
     * <pre>
     * The shortened string. For example, if the original string was 500 bytes long and
     * the limit of the string was 128 bytes, then this value contains the first 128
     * bytes of the 500-byte string. Note that truncation always happens on a
     * character boundary, to ensure that a truncated string is still valid UTF-8.
     * Because it may contain multi-byte characters, the size of the truncated string
     * may be less than the truncation limit.
     * </pre>
     *
     * <code>string value = 1;</code>
     * @return The value.
     */
    @java.lang.Override
    public java.lang.String getValue() {
      return instance.getValue();
    }
    /**
     * <pre>
     * The shortened string. For example, if the original string was 500 bytes long and
     * the limit of the string was 128 bytes, then this value contains the first 128
     * bytes of the 500-byte string. Note that truncation always happens on a
     * character boundary, to ensure that a truncated string is still valid UTF-8.
     * Because it may contain multi-byte characters, the size of the truncated string
     * may be less than the truncation limit.
     * </pre>
     *
     * <code>string value = 1;</code>
     * @return The bytes for value.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getValueBytes() {
      return instance.getValueBytes();
    }
    /**
     * <pre>
     * The shortened string. For example, if the original string was 500 bytes long and
     * the limit of the string was 128 bytes, then this value contains the first 128
     * bytes of the 500-byte string. Note that truncation always happens on a
     * character boundary, to ensure that a truncated string is still valid UTF-8.
     * Because it may contain multi-byte characters, the size of the truncated string
     * may be less than the truncation limit.
     * </pre>
     *
     * <code>string value = 1;</code>
     * @param value The value to set.
     * @return This builder for chaining.
     */
    public Builder setValue(
        java.lang.String value) {
      copyOnWrite();
      instance.setValue(value);
      return this;
    }
    /**
     * <pre>
     * The shortened string. For example, if the original string was 500 bytes long and
     * the limit of the string was 128 bytes, then this value contains the first 128
     * bytes of the 500-byte string. Note that truncation always happens on a
     * character boundary, to ensure that a truncated string is still valid UTF-8.
     * Because it may contain multi-byte characters, the size of the truncated string
     * may be less than the truncation limit.
     * </pre>
     *
     * <code>string value = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearValue() {
      copyOnWrite();
      instance.clearValue();
      return this;
    }
    /**
     * <pre>
     * The shortened string. For example, if the original string was 500 bytes long and
     * the limit of the string was 128 bytes, then this value contains the first 128
     * bytes of the 500-byte string. Note that truncation always happens on a
     * character boundary, to ensure that a truncated string is still valid UTF-8.
     * Because it may contain multi-byte characters, the size of the truncated string
     * may be less than the truncation limit.
     * </pre>
     *
     * <code>string value = 1;</code>
     * @param value The bytes for value to set.
     * @return This builder for chaining.
     */
    public Builder setValueBytes(
        com.google.protobuf.ByteString value) {
      copyOnWrite();
      instance.setValueBytes(value);
      return this;
    }

    /**
     * <pre>
     * The number of bytes removed from the original string. If this
     * value is 0, then the string was not shortened.
     * </pre>
     *
     * <code>int32 truncated_byte_count = 2;</code>
     * @return The truncatedByteCount.
     */
    @java.lang.Override
    public int getTruncatedByteCount() {
      return instance.getTruncatedByteCount();
    }
    /**
     * <pre>
     * The number of bytes removed from the original string. If this
     * value is 0, then the string was not shortened.
     * </pre>
     *
     * <code>int32 truncated_byte_count = 2;</code>
     * @param value The truncatedByteCount to set.
     * @return This builder for chaining.
     */
    public Builder setTruncatedByteCount(int value) {
      copyOnWrite();
      instance.setTruncatedByteCount(value);
      return this;
    }
    /**
     * <pre>
     * The number of bytes removed from the original string. If this
     * value is 0, then the string was not shortened.
     * </pre>
     *
     * <code>int32 truncated_byte_count = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearTruncatedByteCount() {
      copyOnWrite();
      instance.clearTruncatedByteCount();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:opencensus.proto.trace.v1.TruncatableString)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new io.opencensus.proto.trace.v1.TruncatableString();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "value_",
            "truncatedByteCount_",
          };
          java.lang.String info =
              "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\u0208\u0002\u0004" +
              "";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<io.opencensus.proto.trace.v1.TruncatableString> parser = PARSER;
        if (parser == null) {
          synchronized (io.opencensus.proto.trace.v1.TruncatableString.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<io.opencensus.proto.trace.v1.TruncatableString>(
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


  // @@protoc_insertion_point(class_scope:opencensus.proto.trace.v1.TruncatableString)
  private static final io.opencensus.proto.trace.v1.TruncatableString DEFAULT_INSTANCE;
  static {
    TruncatableString defaultInstance = new TruncatableString();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      TruncatableString.class, defaultInstance);
  }

  public static io.opencensus.proto.trace.v1.TruncatableString getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<TruncatableString> PARSER;

  public static com.google.protobuf.Parser<TruncatableString> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

