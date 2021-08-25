// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/agent/trace/v1/trace_service.proto

package io.opencensus.proto.agent.trace.v1;

/**
 * Protobuf type {@code opencensus.proto.agent.trace.v1.ExportTraceServiceRequest}
 */
public  final class ExportTraceServiceRequest extends
    com.google.protobuf.GeneratedMessageLite<
        ExportTraceServiceRequest, ExportTraceServiceRequest.Builder> implements
    // @@protoc_insertion_point(message_implements:opencensus.proto.agent.trace.v1.ExportTraceServiceRequest)
    ExportTraceServiceRequestOrBuilder {
  private ExportTraceServiceRequest() {
    spans_ = emptyProtobufList();
  }
  public static final int NODE_FIELD_NUMBER = 1;
  private io.opencensus.proto.agent.common.v1.Node node_;
  /**
   * <pre>
   * This is required only in the first message on the stream or if the
   * previous sent ExportTraceServiceRequest message has a different Node (e.g.
   * when the same RPC is used to send Spans from multiple Applications).
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
   * previous sent ExportTraceServiceRequest message has a different Node (e.g.
   * when the same RPC is used to send Spans from multiple Applications).
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
   * previous sent ExportTraceServiceRequest message has a different Node (e.g.
   * when the same RPC is used to send Spans from multiple Applications).
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
   * previous sent ExportTraceServiceRequest message has a different Node (e.g.
   * when the same RPC is used to send Spans from multiple Applications).
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
   * previous sent ExportTraceServiceRequest message has a different Node (e.g.
   * when the same RPC is used to send Spans from multiple Applications).
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.Node node = 1;</code>
   */
  private void clearNode() {  node_ = null;
    
  }

  public static final int SPANS_FIELD_NUMBER = 2;
  private com.google.protobuf.Internal.ProtobufList<io.opencensus.proto.trace.v1.Span> spans_;
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  @java.lang.Override
  public java.util.List<io.opencensus.proto.trace.v1.Span> getSpansList() {
    return spans_;
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  public java.util.List<? extends io.opencensus.proto.trace.v1.SpanOrBuilder> 
      getSpansOrBuilderList() {
    return spans_;
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  @java.lang.Override
  public int getSpansCount() {
    return spans_.size();
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  @java.lang.Override
  public io.opencensus.proto.trace.v1.Span getSpans(int index) {
    return spans_.get(index);
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  public io.opencensus.proto.trace.v1.SpanOrBuilder getSpansOrBuilder(
      int index) {
    return spans_.get(index);
  }
  private void ensureSpansIsMutable() {
    com.google.protobuf.Internal.ProtobufList<io.opencensus.proto.trace.v1.Span> tmp = spans_;
    if (!tmp.isModifiable()) {
      spans_ =
          com.google.protobuf.GeneratedMessageLite.mutableCopy(tmp);
     }
  }

  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  private void setSpans(
      int index, io.opencensus.proto.trace.v1.Span value) {
    value.getClass();
  ensureSpansIsMutable();
    spans_.set(index, value);
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  private void addSpans(io.opencensus.proto.trace.v1.Span value) {
    value.getClass();
  ensureSpansIsMutable();
    spans_.add(value);
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  private void addSpans(
      int index, io.opencensus.proto.trace.v1.Span value) {
    value.getClass();
  ensureSpansIsMutable();
    spans_.add(index, value);
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  private void addAllSpans(
      java.lang.Iterable<? extends io.opencensus.proto.trace.v1.Span> values) {
    ensureSpansIsMutable();
    com.google.protobuf.AbstractMessageLite.addAll(
        values, spans_);
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  private void clearSpans() {
    spans_ = emptyProtobufList();
  }
  /**
   * <pre>
   * A list of Spans that belong to the last received Node.
   * </pre>
   *
   * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
   */
  private void removeSpans(int index) {
    ensureSpansIsMutable();
    spans_.remove(index);
  }

  public static final int RESOURCE_FIELD_NUMBER = 3;
  private io.opencensus.proto.resource.v1.Resource resource_;
  /**
   * <pre>
   * The resource for the spans in this message that do not have an explicit
   * resource set.
   * If unset, the most recently set resource in the RPC stream applies. It is
   * valid to never be set within a stream, e.g. when no resource info is known.
   * </pre>
   *
   * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
   */
  @java.lang.Override
  public boolean hasResource() {
    return resource_ != null;
  }
  /**
   * <pre>
   * The resource for the spans in this message that do not have an explicit
   * resource set.
   * If unset, the most recently set resource in the RPC stream applies. It is
   * valid to never be set within a stream, e.g. when no resource info is known.
   * </pre>
   *
   * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
   */
  @java.lang.Override
  public io.opencensus.proto.resource.v1.Resource getResource() {
    return resource_ == null ? io.opencensus.proto.resource.v1.Resource.getDefaultInstance() : resource_;
  }
  /**
   * <pre>
   * The resource for the spans in this message that do not have an explicit
   * resource set.
   * If unset, the most recently set resource in the RPC stream applies. It is
   * valid to never be set within a stream, e.g. when no resource info is known.
   * </pre>
   *
   * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
   */
  private void setResource(io.opencensus.proto.resource.v1.Resource value) {
    value.getClass();
  resource_ = value;
    
    }
  /**
   * <pre>
   * The resource for the spans in this message that do not have an explicit
   * resource set.
   * If unset, the most recently set resource in the RPC stream applies. It is
   * valid to never be set within a stream, e.g. when no resource info is known.
   * </pre>
   *
   * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
   */
  @java.lang.SuppressWarnings({"ReferenceEquality"})
  private void mergeResource(io.opencensus.proto.resource.v1.Resource value) {
    value.getClass();
  if (resource_ != null &&
        resource_ != io.opencensus.proto.resource.v1.Resource.getDefaultInstance()) {
      resource_ =
        io.opencensus.proto.resource.v1.Resource.newBuilder(resource_).mergeFrom(value).buildPartial();
    } else {
      resource_ = value;
    }
    
  }
  /**
   * <pre>
   * The resource for the spans in this message that do not have an explicit
   * resource set.
   * If unset, the most recently set resource in the RPC stream applies. It is
   * valid to never be set within a stream, e.g. when no resource info is known.
   * </pre>
   *
   * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
   */
  private void clearResource() {  resource_ = null;
    
  }

  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * Protobuf type {@code opencensus.proto.agent.trace.v1.ExportTraceServiceRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest, Builder> implements
      // @@protoc_insertion_point(builder_implements:opencensus.proto.agent.trace.v1.ExportTraceServiceRequest)
      io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequestOrBuilder {
    // Construct using io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    /**
     * <pre>
     * This is required only in the first message on the stream or if the
     * previous sent ExportTraceServiceRequest message has a different Node (e.g.
     * when the same RPC is used to send Spans from multiple Applications).
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
     * previous sent ExportTraceServiceRequest message has a different Node (e.g.
     * when the same RPC is used to send Spans from multiple Applications).
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
     * previous sent ExportTraceServiceRequest message has a different Node (e.g.
     * when the same RPC is used to send Spans from multiple Applications).
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
     * previous sent ExportTraceServiceRequest message has a different Node (e.g.
     * when the same RPC is used to send Spans from multiple Applications).
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
     * previous sent ExportTraceServiceRequest message has a different Node (e.g.
     * when the same RPC is used to send Spans from multiple Applications).
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
     * previous sent ExportTraceServiceRequest message has a different Node (e.g.
     * when the same RPC is used to send Spans from multiple Applications).
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
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    @java.lang.Override
    public java.util.List<io.opencensus.proto.trace.v1.Span> getSpansList() {
      return java.util.Collections.unmodifiableList(
          instance.getSpansList());
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    @java.lang.Override
    public int getSpansCount() {
      return instance.getSpansCount();
    }/**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    @java.lang.Override
    public io.opencensus.proto.trace.v1.Span getSpans(int index) {
      return instance.getSpans(index);
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder setSpans(
        int index, io.opencensus.proto.trace.v1.Span value) {
      copyOnWrite();
      instance.setSpans(index, value);
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder setSpans(
        int index, io.opencensus.proto.trace.v1.Span.Builder builderForValue) {
      copyOnWrite();
      instance.setSpans(index,
          builderForValue.build());
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder addSpans(io.opencensus.proto.trace.v1.Span value) {
      copyOnWrite();
      instance.addSpans(value);
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder addSpans(
        int index, io.opencensus.proto.trace.v1.Span value) {
      copyOnWrite();
      instance.addSpans(index, value);
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder addSpans(
        io.opencensus.proto.trace.v1.Span.Builder builderForValue) {
      copyOnWrite();
      instance.addSpans(builderForValue.build());
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder addSpans(
        int index, io.opencensus.proto.trace.v1.Span.Builder builderForValue) {
      copyOnWrite();
      instance.addSpans(index,
          builderForValue.build());
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder addAllSpans(
        java.lang.Iterable<? extends io.opencensus.proto.trace.v1.Span> values) {
      copyOnWrite();
      instance.addAllSpans(values);
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder clearSpans() {
      copyOnWrite();
      instance.clearSpans();
      return this;
    }
    /**
     * <pre>
     * A list of Spans that belong to the last received Node.
     * </pre>
     *
     * <code>repeated .opencensus.proto.trace.v1.Span spans = 2;</code>
     */
    public Builder removeSpans(int index) {
      copyOnWrite();
      instance.removeSpans(index);
      return this;
    }

    /**
     * <pre>
     * The resource for the spans in this message that do not have an explicit
     * resource set.
     * If unset, the most recently set resource in the RPC stream applies. It is
     * valid to never be set within a stream, e.g. when no resource info is known.
     * </pre>
     *
     * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
     */
    @java.lang.Override
    public boolean hasResource() {
      return instance.hasResource();
    }
    /**
     * <pre>
     * The resource for the spans in this message that do not have an explicit
     * resource set.
     * If unset, the most recently set resource in the RPC stream applies. It is
     * valid to never be set within a stream, e.g. when no resource info is known.
     * </pre>
     *
     * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
     */
    @java.lang.Override
    public io.opencensus.proto.resource.v1.Resource getResource() {
      return instance.getResource();
    }
    /**
     * <pre>
     * The resource for the spans in this message that do not have an explicit
     * resource set.
     * If unset, the most recently set resource in the RPC stream applies. It is
     * valid to never be set within a stream, e.g. when no resource info is known.
     * </pre>
     *
     * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
     */
    public Builder setResource(io.opencensus.proto.resource.v1.Resource value) {
      copyOnWrite();
      instance.setResource(value);
      return this;
      }
    /**
     * <pre>
     * The resource for the spans in this message that do not have an explicit
     * resource set.
     * If unset, the most recently set resource in the RPC stream applies. It is
     * valid to never be set within a stream, e.g. when no resource info is known.
     * </pre>
     *
     * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
     */
    public Builder setResource(
        io.opencensus.proto.resource.v1.Resource.Builder builderForValue) {
      copyOnWrite();
      instance.setResource(builderForValue.build());
      return this;
    }
    /**
     * <pre>
     * The resource for the spans in this message that do not have an explicit
     * resource set.
     * If unset, the most recently set resource in the RPC stream applies. It is
     * valid to never be set within a stream, e.g. when no resource info is known.
     * </pre>
     *
     * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
     */
    public Builder mergeResource(io.opencensus.proto.resource.v1.Resource value) {
      copyOnWrite();
      instance.mergeResource(value);
      return this;
    }
    /**
     * <pre>
     * The resource for the spans in this message that do not have an explicit
     * resource set.
     * If unset, the most recently set resource in the RPC stream applies. It is
     * valid to never be set within a stream, e.g. when no resource info is known.
     * </pre>
     *
     * <code>.opencensus.proto.resource.v1.Resource resource = 3;</code>
     */
    public Builder clearResource() {  copyOnWrite();
      instance.clearResource();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:opencensus.proto.agent.trace.v1.ExportTraceServiceRequest)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "node_",
            "spans_",
            io.opencensus.proto.trace.v1.Span.class,
            "resource_",
          };
          java.lang.String info =
              "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0001\u0000\u0001\t\u0002\u001b" +
              "\u0003\t";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest> parser = PARSER;
        if (parser == null) {
          synchronized (io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest>(
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


  // @@protoc_insertion_point(class_scope:opencensus.proto.agent.trace.v1.ExportTraceServiceRequest)
  private static final io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest DEFAULT_INSTANCE;
  static {
    ExportTraceServiceRequest defaultInstance = new ExportTraceServiceRequest();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      ExportTraceServiceRequest.class, defaultInstance);
  }

  public static io.opencensus.proto.agent.trace.v1.ExportTraceServiceRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<ExportTraceServiceRequest> PARSER;

  public static com.google.protobuf.Parser<ExportTraceServiceRequest> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

