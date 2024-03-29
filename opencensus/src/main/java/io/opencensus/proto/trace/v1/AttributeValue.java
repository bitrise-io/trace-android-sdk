// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/trace/v1/trace.proto

package io.opencensus.proto.trace.v1;

/**
 * <pre>
 * The value of an Attribute.
 * </pre>
 *
 * Protobuf type {@code opencensus.proto.trace.v1.AttributeValue}
 */
public  final class AttributeValue extends
    com.google.protobuf.GeneratedMessageLite<
        AttributeValue, AttributeValue.Builder> implements
    // @@protoc_insertion_point(message_implements:opencensus.proto.trace.v1.AttributeValue)
    AttributeValueOrBuilder {
  private AttributeValue() {
  }
  private int valueCase_ = 0;
  private java.lang.Object value_;
  public enum ValueCase {
    STRING_VALUE(1),
    INT_VALUE(2),
    BOOL_VALUE(3),
    DOUBLE_VALUE(4),
    VALUE_NOT_SET(0);
    private final int value;
    private ValueCase(int value) {
      this.value = value;
    }
    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static ValueCase valueOf(int value) {
      return forNumber(value);
    }

    public static ValueCase forNumber(int value) {
      switch (value) {
        case 1: return STRING_VALUE;
        case 2: return INT_VALUE;
        case 3: return BOOL_VALUE;
        case 4: return DOUBLE_VALUE;
        case 0: return VALUE_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  @java.lang.Override
  public ValueCase
  getValueCase() {
    return ValueCase.forNumber(
        valueCase_);
  }

  private void clearValue() {
    valueCase_ = 0;
    value_ = null;
  }

  public static final int STRING_VALUE_FIELD_NUMBER = 1;
  /**
   * <pre>
   * A string up to 256 bytes long.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
   */
  @java.lang.Override
  public boolean hasStringValue() {
    return valueCase_ == 1;
  }
  /**
   * <pre>
   * A string up to 256 bytes long.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
   */
  @java.lang.Override
  public io.opencensus.proto.trace.v1.TruncatableString getStringValue() {
    if (valueCase_ == 1) {
       return (io.opencensus.proto.trace.v1.TruncatableString) value_;
    }
    return io.opencensus.proto.trace.v1.TruncatableString.getDefaultInstance();
  }
  /**
   * <pre>
   * A string up to 256 bytes long.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
   */
  private void setStringValue(io.opencensus.proto.trace.v1.TruncatableString value) {
    value.getClass();
  value_ = value;
    valueCase_ = 1;
  }
  /**
   * <pre>
   * A string up to 256 bytes long.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
   */
  private void mergeStringValue(io.opencensus.proto.trace.v1.TruncatableString value) {
    value.getClass();
  if (valueCase_ == 1 &&
        value_ != io.opencensus.proto.trace.v1.TruncatableString.getDefaultInstance()) {
      value_ = io.opencensus.proto.trace.v1.TruncatableString.newBuilder((io.opencensus.proto.trace.v1.TruncatableString) value_)
          .mergeFrom(value).buildPartial();
    } else {
      value_ = value;
    }
    valueCase_ = 1;
  }
  /**
   * <pre>
   * A string up to 256 bytes long.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
   */
  private void clearStringValue() {
    if (valueCase_ == 1) {
      valueCase_ = 0;
      value_ = null;
    }
  }

  public static final int INT_VALUE_FIELD_NUMBER = 2;
  /**
   * <pre>
   * A 64-bit signed integer.
   * </pre>
   *
   * <code>int64 int_value = 2;</code>
   * @return The intValue.
   */
  @java.lang.Override
  public long getIntValue() {
    if (valueCase_ == 2) {
      return (java.lang.Long) value_;
    }
    return 0L;
  }
  /**
   * <pre>
   * A 64-bit signed integer.
   * </pre>
   *
   * <code>int64 int_value = 2;</code>
   * @param value The intValue to set.
   */
  private void setIntValue(long value) {
    valueCase_ = 2;
    value_ = value;
  }
  /**
   * <pre>
   * A 64-bit signed integer.
   * </pre>
   *
   * <code>int64 int_value = 2;</code>
   */
  private void clearIntValue() {
    if (valueCase_ == 2) {
      valueCase_ = 0;
      value_ = null;
    }
  }

  public static final int BOOL_VALUE_FIELD_NUMBER = 3;
  /**
   * <pre>
   * A Boolean value represented by `true` or `false`.
   * </pre>
   *
   * <code>bool bool_value = 3;</code>
   * @return The boolValue.
   */
  @java.lang.Override
  public boolean getBoolValue() {
    if (valueCase_ == 3) {
      return (java.lang.Boolean) value_;
    }
    return false;
  }
  /**
   * <pre>
   * A Boolean value represented by `true` or `false`.
   * </pre>
   *
   * <code>bool bool_value = 3;</code>
   * @param value The boolValue to set.
   */
  private void setBoolValue(boolean value) {
    valueCase_ = 3;
    value_ = value;
  }
  /**
   * <pre>
   * A Boolean value represented by `true` or `false`.
   * </pre>
   *
   * <code>bool bool_value = 3;</code>
   */
  private void clearBoolValue() {
    if (valueCase_ == 3) {
      valueCase_ = 0;
      value_ = null;
    }
  }

  public static final int DOUBLE_VALUE_FIELD_NUMBER = 4;
  /**
   * <pre>
   * A double value.
   * </pre>
   *
   * <code>double double_value = 4;</code>
   * @return The doubleValue.
   */
  @java.lang.Override
  public double getDoubleValue() {
    if (valueCase_ == 4) {
      return (java.lang.Double) value_;
    }
    return 0D;
  }
  /**
   * <pre>
   * A double value.
   * </pre>
   *
   * <code>double double_value = 4;</code>
   * @param value The doubleValue to set.
   */
  private void setDoubleValue(double value) {
    valueCase_ = 4;
    value_ = value;
  }
  /**
   * <pre>
   * A double value.
   * </pre>
   *
   * <code>double double_value = 4;</code>
   */
  private void clearDoubleValue() {
    if (valueCase_ == 4) {
      valueCase_ = 0;
      value_ = null;
    }
  }

  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.trace.v1.AttributeValue parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(io.opencensus.proto.trace.v1.AttributeValue prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * <pre>
   * The value of an Attribute.
   * </pre>
   *
   * Protobuf type {@code opencensus.proto.trace.v1.AttributeValue}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        io.opencensus.proto.trace.v1.AttributeValue, Builder> implements
      // @@protoc_insertion_point(builder_implements:opencensus.proto.trace.v1.AttributeValue)
      io.opencensus.proto.trace.v1.AttributeValueOrBuilder {
    // Construct using io.opencensus.proto.trace.v1.AttributeValue.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }

    @java.lang.Override
    public ValueCase
        getValueCase() {
      return instance.getValueCase();
    }

    public Builder clearValue() {
      copyOnWrite();
      instance.clearValue();
      return this;
    }


    /**
     * <pre>
     * A string up to 256 bytes long.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
     */
    @java.lang.Override
    public boolean hasStringValue() {
      return instance.hasStringValue();
    }
    /**
     * <pre>
     * A string up to 256 bytes long.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
     */
    @java.lang.Override
    public io.opencensus.proto.trace.v1.TruncatableString getStringValue() {
      return instance.getStringValue();
    }
    /**
     * <pre>
     * A string up to 256 bytes long.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
     */
    public Builder setStringValue(io.opencensus.proto.trace.v1.TruncatableString value) {
      copyOnWrite();
      instance.setStringValue(value);
      return this;
    }
    /**
     * <pre>
     * A string up to 256 bytes long.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
     */
    public Builder setStringValue(
        io.opencensus.proto.trace.v1.TruncatableString.Builder builderForValue) {
      copyOnWrite();
      instance.setStringValue(builderForValue.build());
      return this;
    }
    /**
     * <pre>
     * A string up to 256 bytes long.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
     */
    public Builder mergeStringValue(io.opencensus.proto.trace.v1.TruncatableString value) {
      copyOnWrite();
      instance.mergeStringValue(value);
      return this;
    }
    /**
     * <pre>
     * A string up to 256 bytes long.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TruncatableString string_value = 1;</code>
     */
    public Builder clearStringValue() {
      copyOnWrite();
      instance.clearStringValue();
      return this;
    }

    /**
     * <pre>
     * A 64-bit signed integer.
     * </pre>
     *
     * <code>int64 int_value = 2;</code>
     * @return The intValue.
     */
    @java.lang.Override
    public long getIntValue() {
      return instance.getIntValue();
    }
    /**
     * <pre>
     * A 64-bit signed integer.
     * </pre>
     *
     * <code>int64 int_value = 2;</code>
     * @param value The intValue to set.
     * @return This builder for chaining.
     */
    public Builder setIntValue(long value) {
      copyOnWrite();
      instance.setIntValue(value);
      return this;
    }
    /**
     * <pre>
     * A 64-bit signed integer.
     * </pre>
     *
     * <code>int64 int_value = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearIntValue() {
      copyOnWrite();
      instance.clearIntValue();
      return this;
    }

    /**
     * <pre>
     * A Boolean value represented by `true` or `false`.
     * </pre>
     *
     * <code>bool bool_value = 3;</code>
     * @return The boolValue.
     */
    @java.lang.Override
    public boolean getBoolValue() {
      return instance.getBoolValue();
    }
    /**
     * <pre>
     * A Boolean value represented by `true` or `false`.
     * </pre>
     *
     * <code>bool bool_value = 3;</code>
     * @param value The boolValue to set.
     * @return This builder for chaining.
     */
    public Builder setBoolValue(boolean value) {
      copyOnWrite();
      instance.setBoolValue(value);
      return this;
    }
    /**
     * <pre>
     * A Boolean value represented by `true` or `false`.
     * </pre>
     *
     * <code>bool bool_value = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearBoolValue() {
      copyOnWrite();
      instance.clearBoolValue();
      return this;
    }

    /**
     * <pre>
     * A double value.
     * </pre>
     *
     * <code>double double_value = 4;</code>
     * @return The doubleValue.
     */
    @java.lang.Override
    public double getDoubleValue() {
      return instance.getDoubleValue();
    }
    /**
     * <pre>
     * A double value.
     * </pre>
     *
     * <code>double double_value = 4;</code>
     * @param value The doubleValue to set.
     * @return This builder for chaining.
     */
    public Builder setDoubleValue(double value) {
      copyOnWrite();
      instance.setDoubleValue(value);
      return this;
    }
    /**
     * <pre>
     * A double value.
     * </pre>
     *
     * <code>double double_value = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearDoubleValue() {
      copyOnWrite();
      instance.clearDoubleValue();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:opencensus.proto.trace.v1.AttributeValue)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new io.opencensus.proto.trace.v1.AttributeValue();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "value_",
            "valueCase_",
            io.opencensus.proto.trace.v1.TruncatableString.class,
          };
          java.lang.String info =
              "\u0000\u0004\u0001\u0000\u0001\u0004\u0004\u0000\u0000\u0000\u0001<\u0000\u00025" +
              "\u0000\u0003:\u0000\u00043\u0000";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<io.opencensus.proto.trace.v1.AttributeValue> parser = PARSER;
        if (parser == null) {
          synchronized (io.opencensus.proto.trace.v1.AttributeValue.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<io.opencensus.proto.trace.v1.AttributeValue>(
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


  // @@protoc_insertion_point(class_scope:opencensus.proto.trace.v1.AttributeValue)
  private static final io.opencensus.proto.trace.v1.AttributeValue DEFAULT_INSTANCE;
  static {
    AttributeValue defaultInstance = new AttributeValue();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      AttributeValue.class, defaultInstance);
  }

  public static io.opencensus.proto.trace.v1.AttributeValue getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<AttributeValue> PARSER;

  public static com.google.protobuf.Parser<AttributeValue> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

