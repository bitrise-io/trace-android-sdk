// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: opencensus/proto/agent/common/v1/common.proto

package io.opencensus.proto.agent.common.v1;

/**
 * <pre>
 * Information on OpenCensus Library.
 * </pre>
 *
 * Protobuf type {@code opencensus.proto.agent.common.v1.LibraryInfo}
 */
public  final class LibraryInfo extends
    com.google.protobuf.GeneratedMessageLite<
        LibraryInfo, LibraryInfo.Builder> implements
    // @@protoc_insertion_point(message_implements:opencensus.proto.agent.common.v1.LibraryInfo)
    LibraryInfoOrBuilder {
  private LibraryInfo() {
    exporterVersion_ = "";
    coreLibraryVersion_ = "";
  }
  /**
   * Protobuf enum {@code opencensus.proto.agent.common.v1.LibraryInfo.Language}
   */
  public enum Language
      implements com.google.protobuf.Internal.EnumLite {
    /**
     * <code>LANGUAGE_UNSPECIFIED = 0;</code>
     */
    LANGUAGE_UNSPECIFIED(0),
    /**
     * <code>CPP = 1;</code>
     */
    CPP(1),
    /**
     * <code>C_SHARP = 2;</code>
     */
    C_SHARP(2),
    /**
     * <code>ERLANG = 3;</code>
     */
    ERLANG(3),
    /**
     * <code>GO_LANG = 4;</code>
     */
    GO_LANG(4),
    /**
     * <code>JAVA = 5;</code>
     */
    JAVA(5),
    /**
     * <code>NODE_JS = 6;</code>
     */
    NODE_JS(6),
    /**
     * <code>PHP = 7;</code>
     */
    PHP(7),
    /**
     * <code>PYTHON = 8;</code>
     */
    PYTHON(8),
    /**
     * <code>RUBY = 9;</code>
     */
    RUBY(9),
    /**
     * <code>WEB_JS = 10;</code>
     */
    WEB_JS(10),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>LANGUAGE_UNSPECIFIED = 0;</code>
     */
    public static final int LANGUAGE_UNSPECIFIED_VALUE = 0;
    /**
     * <code>CPP = 1;</code>
     */
    public static final int CPP_VALUE = 1;
    /**
     * <code>C_SHARP = 2;</code>
     */
    public static final int C_SHARP_VALUE = 2;
    /**
     * <code>ERLANG = 3;</code>
     */
    public static final int ERLANG_VALUE = 3;
    /**
     * <code>GO_LANG = 4;</code>
     */
    public static final int GO_LANG_VALUE = 4;
    /**
     * <code>JAVA = 5;</code>
     */
    public static final int JAVA_VALUE = 5;
    /**
     * <code>NODE_JS = 6;</code>
     */
    public static final int NODE_JS_VALUE = 6;
    /**
     * <code>PHP = 7;</code>
     */
    public static final int PHP_VALUE = 7;
    /**
     * <code>PYTHON = 8;</code>
     */
    public static final int PYTHON_VALUE = 8;
    /**
     * <code>RUBY = 9;</code>
     */
    public static final int RUBY_VALUE = 9;
    /**
     * <code>WEB_JS = 10;</code>
     */
    public static final int WEB_JS_VALUE = 10;


    @java.lang.Override
    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The number of the enum to look for.
     * @return The enum associated with the given number.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static Language valueOf(int value) {
      return forNumber(value);
    }

    public static Language forNumber(int value) {
      switch (value) {
        case 0: return LANGUAGE_UNSPECIFIED;
        case 1: return CPP;
        case 2: return C_SHARP;
        case 3: return ERLANG;
        case 4: return GO_LANG;
        case 5: return JAVA;
        case 6: return NODE_JS;
        case 7: return PHP;
        case 8: return PYTHON;
        case 9: return RUBY;
        case 10: return WEB_JS;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Language>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Language> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Language>() {
            @java.lang.Override
            public Language findValueByNumber(int number) {
              return Language.forNumber(number);
            }
          };

    public static com.google.protobuf.Internal.EnumVerifier 
        internalGetVerifier() {
      return LanguageVerifier.INSTANCE;
    }

    private static final class LanguageVerifier implements 
         com.google.protobuf.Internal.EnumVerifier { 
            static final com.google.protobuf.Internal.EnumVerifier           INSTANCE = new LanguageVerifier();
            @java.lang.Override
            public boolean isInRange(int number) {
              return Language.forNumber(number) != null;
            }
          };

    private final int value;

    private Language(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:opencensus.proto.agent.common.v1.LibraryInfo.Language)
  }

  public static final int LANGUAGE_FIELD_NUMBER = 1;
  private int language_;
  /**
   * <pre>
   * Language of OpenCensus Library.
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
   * @return The enum numeric value on the wire for language.
   */
  @java.lang.Override
  public int getLanguageValue() {
    return language_;
  }
  /**
   * <pre>
   * Language of OpenCensus Library.
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
   * @return The language.
   */
  @java.lang.Override
  public io.opencensus.proto.agent.common.v1.LibraryInfo.Language getLanguage() {
    io.opencensus.proto.agent.common.v1.LibraryInfo.Language result = io.opencensus.proto.agent.common.v1.LibraryInfo.Language.forNumber(language_);
    return result == null ? io.opencensus.proto.agent.common.v1.LibraryInfo.Language.UNRECOGNIZED : result;
  }
  /**
   * <pre>
   * Language of OpenCensus Library.
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
   * @param value The enum numeric value on the wire for language to set.
   */
  private void setLanguageValue(int value) {
      language_ = value;
  }
  /**
   * <pre>
   * Language of OpenCensus Library.
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
   * @param value The language to set.
   */
  private void setLanguage(io.opencensus.proto.agent.common.v1.LibraryInfo.Language value) {
    language_ = value.getNumber();
    
  }
  /**
   * <pre>
   * Language of OpenCensus Library.
   * </pre>
   *
   * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
   */
  private void clearLanguage() {
    
    language_ = 0;
  }

  public static final int EXPORTER_VERSION_FIELD_NUMBER = 2;
  private java.lang.String exporterVersion_;
  /**
   * <pre>
   * Version of Agent exporter of Library.
   * </pre>
   *
   * <code>string exporter_version = 2;</code>
   * @return The exporterVersion.
   */
  @java.lang.Override
  public java.lang.String getExporterVersion() {
    return exporterVersion_;
  }
  /**
   * <pre>
   * Version of Agent exporter of Library.
   * </pre>
   *
   * <code>string exporter_version = 2;</code>
   * @return The bytes for exporterVersion.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getExporterVersionBytes() {
    return com.google.protobuf.ByteString.copyFromUtf8(exporterVersion_);
  }
  /**
   * <pre>
   * Version of Agent exporter of Library.
   * </pre>
   *
   * <code>string exporter_version = 2;</code>
   * @param value The exporterVersion to set.
   */
  private void setExporterVersion(
      java.lang.String value) {
    value.getClass();
  
    exporterVersion_ = value;
  }
  /**
   * <pre>
   * Version of Agent exporter of Library.
   * </pre>
   *
   * <code>string exporter_version = 2;</code>
   */
  private void clearExporterVersion() {
    
    exporterVersion_ = getDefaultInstance().getExporterVersion();
  }
  /**
   * <pre>
   * Version of Agent exporter of Library.
   * </pre>
   *
   * <code>string exporter_version = 2;</code>
   * @param value The bytes for exporterVersion to set.
   */
  private void setExporterVersionBytes(
      com.google.protobuf.ByteString value) {
    checkByteStringIsUtf8(value);
    exporterVersion_ = value.toStringUtf8();
    
  }

  public static final int CORE_LIBRARY_VERSION_FIELD_NUMBER = 3;
  private java.lang.String coreLibraryVersion_;
  /**
   * <pre>
   * Version of OpenCensus Library.
   * </pre>
   *
   * <code>string core_library_version = 3;</code>
   * @return The coreLibraryVersion.
   */
  @java.lang.Override
  public java.lang.String getCoreLibraryVersion() {
    return coreLibraryVersion_;
  }
  /**
   * <pre>
   * Version of OpenCensus Library.
   * </pre>
   *
   * <code>string core_library_version = 3;</code>
   * @return The bytes for coreLibraryVersion.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getCoreLibraryVersionBytes() {
    return com.google.protobuf.ByteString.copyFromUtf8(coreLibraryVersion_);
  }
  /**
   * <pre>
   * Version of OpenCensus Library.
   * </pre>
   *
   * <code>string core_library_version = 3;</code>
   * @param value The coreLibraryVersion to set.
   */
  private void setCoreLibraryVersion(
      java.lang.String value) {
    value.getClass();
  
    coreLibraryVersion_ = value;
  }
  /**
   * <pre>
   * Version of OpenCensus Library.
   * </pre>
   *
   * <code>string core_library_version = 3;</code>
   */
  private void clearCoreLibraryVersion() {
    
    coreLibraryVersion_ = getDefaultInstance().getCoreLibraryVersion();
  }
  /**
   * <pre>
   * Version of OpenCensus Library.
   * </pre>
   *
   * <code>string core_library_version = 3;</code>
   * @param value The bytes for coreLibraryVersion to set.
   */
  private void setCoreLibraryVersionBytes(
      com.google.protobuf.ByteString value) {
    checkByteStringIsUtf8(value);
    coreLibraryVersion_ = value.toStringUtf8();
    
  }

  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static io.opencensus.proto.agent.common.v1.LibraryInfo parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(io.opencensus.proto.agent.common.v1.LibraryInfo prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * <pre>
   * Information on OpenCensus Library.
   * </pre>
   *
   * Protobuf type {@code opencensus.proto.agent.common.v1.LibraryInfo}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        io.opencensus.proto.agent.common.v1.LibraryInfo, Builder> implements
      // @@protoc_insertion_point(builder_implements:opencensus.proto.agent.common.v1.LibraryInfo)
      io.opencensus.proto.agent.common.v1.LibraryInfoOrBuilder {
    // Construct using io.opencensus.proto.agent.common.v1.LibraryInfo.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    /**
     * <pre>
     * Language of OpenCensus Library.
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
     * @return The enum numeric value on the wire for language.
     */
    @java.lang.Override
    public int getLanguageValue() {
      return instance.getLanguageValue();
    }
    /**
     * <pre>
     * Language of OpenCensus Library.
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
     * @param value The language to set.
     * @return This builder for chaining.
     */
    public Builder setLanguageValue(int value) {
      copyOnWrite();
      instance.setLanguageValue(value);
      return this;
    }
    /**
     * <pre>
     * Language of OpenCensus Library.
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
     * @return The language.
     */
    @java.lang.Override
    public io.opencensus.proto.agent.common.v1.LibraryInfo.Language getLanguage() {
      return instance.getLanguage();
    }
    /**
     * <pre>
     * Language of OpenCensus Library.
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
     * @param value The enum numeric value on the wire for language to set.
     * @return This builder for chaining.
     */
    public Builder setLanguage(io.opencensus.proto.agent.common.v1.LibraryInfo.Language value) {
      copyOnWrite();
      instance.setLanguage(value);
      return this;
    }
    /**
     * <pre>
     * Language of OpenCensus Library.
     * </pre>
     *
     * <code>.opencensus.proto.agent.common.v1.LibraryInfo.Language language = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearLanguage() {
      copyOnWrite();
      instance.clearLanguage();
      return this;
    }

    /**
     * <pre>
     * Version of Agent exporter of Library.
     * </pre>
     *
     * <code>string exporter_version = 2;</code>
     * @return The exporterVersion.
     */
    @java.lang.Override
    public java.lang.String getExporterVersion() {
      return instance.getExporterVersion();
    }
    /**
     * <pre>
     * Version of Agent exporter of Library.
     * </pre>
     *
     * <code>string exporter_version = 2;</code>
     * @return The bytes for exporterVersion.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getExporterVersionBytes() {
      return instance.getExporterVersionBytes();
    }
    /**
     * <pre>
     * Version of Agent exporter of Library.
     * </pre>
     *
     * <code>string exporter_version = 2;</code>
     * @param value The exporterVersion to set.
     * @return This builder for chaining.
     */
    public Builder setExporterVersion(
        java.lang.String value) {
      copyOnWrite();
      instance.setExporterVersion(value);
      return this;
    }
    /**
     * <pre>
     * Version of Agent exporter of Library.
     * </pre>
     *
     * <code>string exporter_version = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearExporterVersion() {
      copyOnWrite();
      instance.clearExporterVersion();
      return this;
    }
    /**
     * <pre>
     * Version of Agent exporter of Library.
     * </pre>
     *
     * <code>string exporter_version = 2;</code>
     * @param value The bytes for exporterVersion to set.
     * @return This builder for chaining.
     */
    public Builder setExporterVersionBytes(
        com.google.protobuf.ByteString value) {
      copyOnWrite();
      instance.setExporterVersionBytes(value);
      return this;
    }

    /**
     * <pre>
     * Version of OpenCensus Library.
     * </pre>
     *
     * <code>string core_library_version = 3;</code>
     * @return The coreLibraryVersion.
     */
    @java.lang.Override
    public java.lang.String getCoreLibraryVersion() {
      return instance.getCoreLibraryVersion();
    }
    /**
     * <pre>
     * Version of OpenCensus Library.
     * </pre>
     *
     * <code>string core_library_version = 3;</code>
     * @return The bytes for coreLibraryVersion.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getCoreLibraryVersionBytes() {
      return instance.getCoreLibraryVersionBytes();
    }
    /**
     * <pre>
     * Version of OpenCensus Library.
     * </pre>
     *
     * <code>string core_library_version = 3;</code>
     * @param value The coreLibraryVersion to set.
     * @return This builder for chaining.
     */
    public Builder setCoreLibraryVersion(
        java.lang.String value) {
      copyOnWrite();
      instance.setCoreLibraryVersion(value);
      return this;
    }
    /**
     * <pre>
     * Version of OpenCensus Library.
     * </pre>
     *
     * <code>string core_library_version = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearCoreLibraryVersion() {
      copyOnWrite();
      instance.clearCoreLibraryVersion();
      return this;
    }
    /**
     * <pre>
     * Version of OpenCensus Library.
     * </pre>
     *
     * <code>string core_library_version = 3;</code>
     * @param value The bytes for coreLibraryVersion to set.
     * @return This builder for chaining.
     */
    public Builder setCoreLibraryVersionBytes(
        com.google.protobuf.ByteString value) {
      copyOnWrite();
      instance.setCoreLibraryVersionBytes(value);
      return this;
    }

    // @@protoc_insertion_point(builder_scope:opencensus.proto.agent.common.v1.LibraryInfo)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new io.opencensus.proto.agent.common.v1.LibraryInfo();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "language_",
            "exporterVersion_",
            "coreLibraryVersion_",
          };
          java.lang.String info =
              "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\f\u0002\u0208" +
              "\u0003\u0208";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<io.opencensus.proto.agent.common.v1.LibraryInfo> parser = PARSER;
        if (parser == null) {
          synchronized (io.opencensus.proto.agent.common.v1.LibraryInfo.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<io.opencensus.proto.agent.common.v1.LibraryInfo>(
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


  // @@protoc_insertion_point(class_scope:opencensus.proto.agent.common.v1.LibraryInfo)
  private static final io.opencensus.proto.agent.common.v1.LibraryInfo DEFAULT_INSTANCE;
  static {
    LibraryInfo defaultInstance = new LibraryInfo();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      LibraryInfo.class, defaultInstance);
  }

  public static io.opencensus.proto.agent.common.v1.LibraryInfo getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<LibraryInfo> PARSER;

  public static com.google.protobuf.Parser<LibraryInfo> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

