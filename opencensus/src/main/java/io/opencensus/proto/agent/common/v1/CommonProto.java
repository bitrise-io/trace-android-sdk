// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/agent/common/v1/common.proto

package io.opencensus.proto.agent.common.v1;

public final class CommonProto {
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_opencensus_proto_agent_common_v1_Node_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_opencensus_proto_agent_common_v1_Node_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_opencensus_proto_agent_common_v1_Node_AttributesEntry_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_opencensus_proto_agent_common_v1_Node_AttributesEntry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_opencensus_proto_agent_common_v1_ProcessIdentifier_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_opencensus_proto_agent_common_v1_ProcessIdentifier_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_opencensus_proto_agent_common_v1_LibraryInfo_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_opencensus_proto_agent_common_v1_LibraryInfo_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_opencensus_proto_agent_common_v1_ServiceInfo_descriptor;
  static final
  com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_opencensus_proto_agent_common_v1_ServiceInfo_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.FileDescriptor
      descriptor;

  static {
    java.lang.String[] descriptorData = {
        "\n-opencensus/proto/agent/common/v1/commo" +
            "n.proto\022 opencensus.proto.agent.common.v" +
            "1\032\037google/protobuf/timestamp.proto\"\330\002\n\004N" +
            "ode\022G\n\nidentifier\030\001 \001(\01323.opencensus.pro" +
            "to.agent.common.v1.ProcessIdentifier\022C\n\014" +
            "library_info\030\002 \001(\0132-.opencensus.proto.ag" +
            "ent.common.v1.LibraryInfo\022C\n\014service_inf" +
            "o\030\003 \001(\0132-.opencensus.proto.agent.common." +
            "v1.ServiceInfo\022J\n\nattributes\030\004 \003(\01326.ope" +
            "ncensus.proto.agent.common.v1.Node.Attri" +
            "butesEntry\0321\n\017AttributesEntry\022\013\n\003key\030\001 \001" +
            "(\t\022\r\n\005value\030\002 \001(\t:\0028\001\"h\n\021ProcessIdentifi" +
            "er\022\021\n\thost_name\030\001 \001(\t\022\013\n\003pid\030\002 \001" +
            "(\r\0223\n\017st" +
            "art_timestamp\030\003 \001(\0132\032.google.protobuf.Ti" +
            "mestamp\"\247\002\n\013LibraryInfo\022H\n\010language\030\001 \001(" +
            "\01626.opencensus.proto.agent.common.v1.Lib" +
            "raryInfo.Language\022\030\n\020exporter_version\030\002 " +
            "\001(\t\022\034\n\024core_library_version\030\003 \001(\t\"\225\001\n\010La" +
            "nguage\022\030\n\024LANGUAGE_UNSPECIFIED\020\000\022\007\n\003CPP\020" +
            "\001\022\013\n\007C_SHARP\020\002\022\n\n\006ERLANG\020\003\022\013\n\007GO_LANG\020" +
            "\004\022" +
            "\010\n\004JAVA\020\005\022\013\n\007NODE_JS\020\006\022\007\n\003PHP\020\007\022\n\n" +
            "\006PYTHO" +
            "N\020\010\022\010\n\004RUBY\020\t\022\n\n\006WEB_JS\020\n\"\033\n\013ServiceInfo" +
            "\022\014\n\004name\030\001 \001(\tB\242\001\n#io.opencensus.proto.a" +
            "gent.common.v1B\013CommonProtoP\001ZIgithub.co" +
            "m/census-instrumentation/opencensus-prot" +
            "o/gen-go/agent/common/v1\352\002 OpenCensus.Pr" +
            "oto.Agent.Common.V1b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
        .internalBuildGeneratedFileFrom(descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
                com.google.protobuf.TimestampProto.getDescriptor(),
            });
    internal_static_opencensus_proto_agent_common_v1_Node_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_opencensus_proto_agent_common_v1_Node_fieldAccessorTable = new
        com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_opencensus_proto_agent_common_v1_Node_descriptor,
        new java.lang.String[] {"Identifier", "LibraryInfo", "ServiceInfo", "Attributes",});
    internal_static_opencensus_proto_agent_common_v1_Node_AttributesEntry_descriptor =
        internal_static_opencensus_proto_agent_common_v1_Node_descriptor.getNestedTypes().get(0);
    internal_static_opencensus_proto_agent_common_v1_Node_AttributesEntry_fieldAccessorTable = new
        com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_opencensus_proto_agent_common_v1_Node_AttributesEntry_descriptor,
        new java.lang.String[] {"Key", "Value",});
    internal_static_opencensus_proto_agent_common_v1_ProcessIdentifier_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_opencensus_proto_agent_common_v1_ProcessIdentifier_fieldAccessorTable = new
        com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_opencensus_proto_agent_common_v1_ProcessIdentifier_descriptor,
        new java.lang.String[] {"HostName", "Pid", "StartTimestamp",});
    internal_static_opencensus_proto_agent_common_v1_LibraryInfo_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_opencensus_proto_agent_common_v1_LibraryInfo_fieldAccessorTable = new
        com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_opencensus_proto_agent_common_v1_LibraryInfo_descriptor,
        new java.lang.String[] {"Language", "ExporterVersion", "CoreLibraryVersion",});
    internal_static_opencensus_proto_agent_common_v1_ServiceInfo_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_opencensus_proto_agent_common_v1_ServiceInfo_fieldAccessorTable = new
        com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_opencensus_proto_agent_common_v1_ServiceInfo_descriptor,
        new java.lang.String[] {"Name",});
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  private CommonProto() {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
  getDescriptor() {
    return descriptor;
  }

  // @@protoc_insertion_point(outer_class_scope)
}
