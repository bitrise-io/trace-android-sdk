// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/agent/trace/v1/trace_service.proto

package io.opencensus.proto.agent.trace.v1;

/**
 * Protobuf type {@code opencensus.proto.agent.trace.v1.CurrentLibraryConfig}
 */
public  final class CurrentLibraryConfig extends
    com.google.protobuf.GeneratedMessageLite<
        CurrentLibraryConfig, CurrentLibraryConfig.Builder> implements
    // @@protoc_insertion_point(message_implements:opencensus.proto.agent.trace.v1.CurrentLibraryConfig)
    CurrentLibraryConfigOrBuilder {
  private CurrentLibraryConfig() {
  }
  public static final int NODE_FIELD_NUMBER = 1;
  private io.opencensus.proto.agent.common.v1.Node node_;
  /**
   * <pre>
   * This is required only in the first message on the stream or if the
   * previous sent CurrentLibraryConfig message has a different Node (e.g.
   * when the same RPC is used to configure multiple Applications).
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
   */
  @java.lang.Override
  public boolean hasNode() {
    return node_ != null;
  }
  /**
   * <pre>
   * This is required only in the first message on the stream or if the
   * previous sent CurrentLibraryConfig message has a different Node (e.g.
   * when the same RPC is used to configure multiple Applications).
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
   */
  @java.lang.Override
  public io.opencensus.proto.agent.common.v1.Node getNode() {
    return node_ == null ? io.opencensus.proto.agent.common.v1.Node.getDefaultInstance() : node_;
  }
  /**
   * <pre>
   * This is required only in the first message on the stream or if the
   * previous sent CurrentLibraryConfig message has a different Node (e.g.
   * when the same RPC is used to configure multiple Applications).
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
   */
  private void setNode(io.opencensus.proto.agent.common.v1.Node value) {
    value.getClass();
  node_ = value;
    
    }
  /**
   * <pre>
   * This is required only in the first message on the stream or if the
   * previous sent CurrentLibraryConfig message has a different Node (e.g.
   * when the same RPC is used to configure multiple Applications).
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
   */
  @java.lang.SuppressWarnings({"ReferenceEquality"})
  private void mergeNode(io.opencensus.proto.agent.common.v1.Node value) {
    value.getClass();
  if (node_ != null &&
        node_ != io.opencensus.proto.agent.common.v1.Node.getDefaultInstance()) {
      node_ =
        io.opencensus.proto.agent.common.v1.Node.newBuilder(node_).mergeFrom(value).buildPartial();
    } else {
      node_ = value;
    }
    
  }
  /**
   * <pre>
   * This is required only in the first message on the stream or if the
   * previous sent CurrentLibraryConfig message has a different Node (e.g.
   * when the same RPC is used to configure multiple Applications).
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
   */
  private void clearNode() {  node_ = null;
    
  }

  public static final int CONFIG_FIELD_NUMBER = 2;
  private io.opencensus.proto.trace.v1.TraceConfig config_;
  /**
   * <pre>
   * Current configuration.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
   */
  @java.lang.Override
  public boolean hasConfig() {
    return config_ != null;
  }
  /**
   * <pre>
   * Current configuration.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
   */
  @java.lang.Override
  public io.opencensus.proto.trace.v1.TraceConfig getConfig() {
    return config_ == null ? io.opencensus.proto.trace.v1.TraceConfig.getDefaultInstance() : config_;
  }
  /**
   * <pre>
   * Current configuration.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
   */
  private void setConfig(io.opencensus.proto.trace.v1.TraceConfig value) {
    value.getClass();
  config_ = value;
    
    }
  /**
   * <pre>
   * Current configuration.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
   */
  @java.lang.SuppressWarnings({"ReferenceEquality"})
  private void mergeConfig(io.opencensus.proto.trace.v1.TraceConfig value) {
    value.getClass();
  if (config_ != null &&
        config_ != io.opencensus.proto.trace.v1.TraceConfig.getDefaultInstance()) {
      config_ =
        io.opencensus.proto.trace.v1.TraceConfig.newBuilder(config_).mergeFrom(value).buildPartial();
    } else {
      config_ = value;
    }
    
  }
  /**
   * <pre>
   * Current configuration.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
   */
  private void clearConfig() {  config_ = null;
    
  }

  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * Protobuf type {@code opencensus.proto.agent.trace.v1.CurrentLibraryConfig}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig, Builder> implements
      // @@protoc_insertion_point(builder_implements:opencensus.proto.agent.trace.v1.CurrentLibraryConfig)
      io.opencensus.proto.agent.trace.v1.CurrentLibraryConfigOrBuilder {
    // Construct using io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    /**
     * <pre>
     * This is required only in the first message on the stream or if the
     * previous sent CurrentLibraryConfig message has a different Node (e.g.
     * when the same RPC is used to configure multiple Applications).
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
     */
    @java.lang.Override
    public boolean hasNode() {
      return instance.hasNode();
    }
    /**
     * <pre>
     * This is required only in the first message on the stream or if the
     * previous sent CurrentLibraryConfig message has a different Node (e.g.
     * when the same RPC is used to configure multiple Applications).
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
     */
    @java.lang.Override
    public io.opencensus.proto.agent.common.v1.Node getNode() {
      return instance.getNode();
    }
    /**
     * <pre>
     * This is required only in the first message on the stream or if the
     * previous sent CurrentLibraryConfig message has a different Node (e.g.
     * when the same RPC is used to configure multiple Applications).
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
     */
    public Builder setNode(io.opencensus.proto.agent.common.v1.Node value) {
      copyOnWrite();
      instance.setNode(value);
      return this;
      }
    /**
     * <pre>
     * This is required only in the first message on the stream or if the
     * previous sent CurrentLibraryConfig message has a different Node (e.g.
     * when the same RPC is used to configure multiple Applications).
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
     */
    public Builder setNode(
        io.opencensus.proto.agent.common.v1.Node.Builder builderForValue) {
      copyOnWrite();
      instance.setNode(builderForValue.build());
      return this;
    }
    /**
     * <pre>
     * This is required only in the first message on the stream or if the
     * previous sent CurrentLibraryConfig message has a different Node (e.g.
     * when the same RPC is used to configure multiple Applications).
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
     */
    public Builder mergeNode(io.opencensus.proto.agent.common.v1.Node value) {
      copyOnWrite();
      instance.mergeNode(value);
      return this;
    }
    /**
     * <pre>
     * This is required only in the first message on the stream or if the
     * previous sent CurrentLibraryConfig message has a different Node (e.g.
     * when the same RPC is used to configure multiple Applications).
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
     */
    public Builder clearNode() {  copyOnWrite();
      instance.clearNode();
      return this;
    }

    /**
     * <pre>
     * Current configuration.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
     */
    @java.lang.Override
    public boolean hasConfig() {
      return instance.hasConfig();
    }
    /**
     * <pre>
     * Current configuration.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
     */
    @java.lang.Override
    public io.opencensus.proto.trace.v1.TraceConfig getConfig() {
      return instance.getConfig();
    }
    /**
     * <pre>
     * Current configuration.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
     */
    public Builder setConfig(io.opencensus.proto.trace.v1.TraceConfig value) {
      copyOnWrite();
      instance.setConfig(value);
      return this;
      }
    /**
     * <pre>
     * Current configuration.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
     */
    public Builder setConfig(
        io.opencensus.proto.trace.v1.TraceConfig.Builder builderForValue) {
      copyOnWrite();
      instance.setConfig(builderForValue.build());
      return this;
    }
    /**
     * <pre>
     * Current configuration.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
     */
    public Builder mergeConfig(io.opencensus.proto.trace.v1.TraceConfig value) {
      copyOnWrite();
      instance.mergeConfig(value);
      return this;
    }
    /**
     * <pre>
     * Current configuration.
     * </pre>
     *
     * <code>.opencensus.proto.trace.v1.TraceConfig config = 2;</code>
     */
    public Builder clearConfig() {  copyOnWrite();
      instance.clearConfig();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:opencensus.proto.agent.trace.v1.CurrentLibraryConfig)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "node_",
            "config_",
          };
          java.lang.String info =
              "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\t\u0002\t";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig> parser = PARSER;
        if (parser == null) {
          synchronized (io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig>(
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


  // @@protoc_insertion_point(class_scope:opencensus.proto.agent.trace.v1.CurrentLibraryConfig)
  private static final io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig DEFAULT_INSTANCE;
  static {
    CurrentLibraryConfig defaultInstance = new CurrentLibraryConfig();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      CurrentLibraryConfig.class, defaultInstance);
  }

  public static io.opencensus.proto.agent.trace.v1.CurrentLibraryConfig getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<CurrentLibraryConfig> PARSER;

  public static com.google.protobuf.Parser<CurrentLibraryConfig> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

