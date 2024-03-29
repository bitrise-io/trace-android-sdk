// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/metrics/v1/metrics.proto

package io.opencensus.proto.metrics.v1;

public interface LabelValueOrBuilder extends
    // @@protoc_insertion_point(interface_extends:opencensus.proto.metrics.v1.LabelValue)
    com.google.protobuf.MessageLiteOrBuilder {

  /**
   * <pre>
   * The value for the label.
   * </pre>
   *
   * <code>string value = 1;</code>
   * @return The value.
   */
  java.lang.String getValue();
  /**
   * <pre>
   * The value for the label.
   * </pre>
   *
   * <code>string value = 1;</code>
   * @return The bytes for value.
   */
  com.google.protobuf.ByteString
      getValueBytes();

  /**
   * <pre>
   * If false the value field is ignored and considered not set.
   * This is used to differentiate a missing label from an empty string.
   * </pre>
   *
   * <code>bool has_value = 2;</code>
   * @return The hasValue.
   */
  boolean getHasValue();
}
