// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/stats/v1/stats.proto

package io.opencensus.proto.stats.v1;

/**
 * Protobuf type {@code opencensus.proto.stats.v1.LastValueAggregation}
 */
public  final class LastValueAggregation extends
    com.google.protobuf.GeneratedMessageLite<
        LastValueAggregation, LastValueAggregation.Builder> implements
    // @@protoc_insertion_point(message_implements:opencensus.proto.stats.v1.LastValueAggregation)
    LastValueAggregationOrBuilder {
  private LastValueAggregation() {
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.stats.v1.LastValueAggregation parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(io.opencensus.proto.stats.v1.LastValueAggregation prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * Protobuf type {@code opencensus.proto.stats.v1.LastValueAggregation}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        io.opencensus.proto.stats.v1.LastValueAggregation, Builder> implements
      // @@protoc_insertion_point(builder_implements:opencensus.proto.stats.v1.LastValueAggregation)
      io.opencensus.proto.stats.v1.LastValueAggregationOrBuilder {
    // Construct using io.opencensus.proto.stats.v1.LastValueAggregation.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    // @@protoc_insertion_point(builder_scope:opencensus.proto.stats.v1.LastValueAggregation)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new io.opencensus.proto.stats.v1.LastValueAggregation();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = null;java.lang.String info =
              "\u0000\u0000";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<io.opencensus.proto.stats.v1.LastValueAggregation> parser = PARSER;
        if (parser == null) {
          synchronized (io.opencensus.proto.stats.v1.LastValueAggregation.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<io.opencensus.proto.stats.v1.LastValueAggregation>(
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


  // @@protoc_insertion_point(class_scope:opencensus.proto.stats.v1.LastValueAggregation)
  private static final io.opencensus.proto.stats.v1.LastValueAggregation DEFAULT_INSTANCE;
  static {
    LastValueAggregation defaultInstance = new LastValueAggregation();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      LastValueAggregation.class, defaultInstance);
  }

  public static io.opencensus.proto.stats.v1.LastValueAggregation getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<LastValueAggregation> PARSER;

  public static com.google.protobuf.Parser<LastValueAggregation> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

