// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/trace/v1/trace.proto

package io.opencensus.proto.trace.v1;

public interface ModuleOrBuilder extends
    // @@protoc_insertion_point(interface_extends:opencensus.proto.trace.v1.Module)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * TODO: document the meaning of this field.
   * For example: main binary, kernel modules, and dynamic libraries
   * such as libc.so, sharedlib.so.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString module = 1;</code>
   *
   * @return Whether the module field is set.
   */
  boolean hasModule();

  /**
   * <pre>
   * TODO: document the meaning of this field.
   * For example: main binary, kernel modules, and dynamic libraries
   * such as libc.so, sharedlib.so.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString module = 1;</code>
   *
   * @return The module.
   */
  io.opencensus.proto.trace.v1.TruncatableString getModule();

  /**
   * <pre>
   * TODO: document the meaning of this field.
   * For example: main binary, kernel modules, and dynamic libraries
   * such as libc.so, sharedlib.so.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString module = 1;</code>
   */
  io.opencensus.proto.trace.v1.TruncatableStringOrBuilder getModuleOrBuilder();

  /**
   * <pre>
   * A unique identifier for the module, usually a hash of its
   * contents.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString build_id = 2;</code>
   *
   * @return Whether the buildId field is set.
   */
  boolean hasBuildId();

  /**
   * <pre>
   * A unique identifier for the module, usually a hash of its
   * contents.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString build_id = 2;</code>
   *
   * @return The buildId.
   */
  io.opencensus.proto.trace.v1.TruncatableString getBuildId();

  /**
   * <pre>
   * A unique identifier for the module, usually a hash of its
   * contents.
   * </pre>
   *
   * <code>.opencensus.proto.trace.v1.TruncatableString build_id = 2;</code>
   */
  io.opencensus.proto.trace.v1.TruncatableStringOrBuilder getBuildIdOrBuilder();
}
