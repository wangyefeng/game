// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: common.proto
// Protobuf Java Version: 4.27.1

package org.wangyefeng.game.proto;

public final class Common {
  private Common() {}
  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 27,
      /* patch= */ 1,
      /* suffix= */ "",
      Common.class.getName());
  }
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface PbIntOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.wangyefeng.game.proto.PbInt)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 val = 1;</code>
     * @return The val.
     */
    int getVal();
  }
  /**
   * Protobuf type {@code org.wangyefeng.game.proto.PbInt}
   */
  public static final class PbInt extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:org.wangyefeng.game.proto.PbInt)
      PbIntOrBuilder {
  private static final long serialVersionUID = 0L;
    static {
      com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
        com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
        /* major= */ 4,
        /* minor= */ 27,
        /* patch= */ 1,
        /* suffix= */ "",
        PbInt.class.getName());
    }
    // Use PbInt.newBuilder() to construct.
    private PbInt(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
    }
    private PbInt() {
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbInt_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbInt_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.wangyefeng.game.proto.Common.PbInt.class, org.wangyefeng.game.proto.Common.PbInt.Builder.class);
    }

    public static final int VAL_FIELD_NUMBER = 1;
    private int val_ = 0;
    /**
     * <code>int32 val = 1;</code>
     * @return The val.
     */
    @java.lang.Override
    public int getVal() {
      return val_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (val_ != 0) {
        output.writeInt32(1, val_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (val_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, val_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.wangyefeng.game.proto.Common.PbInt)) {
        return super.equals(obj);
      }
      org.wangyefeng.game.proto.Common.PbInt other = (org.wangyefeng.game.proto.Common.PbInt) obj;

      if (getVal()
          != other.getVal()) return false;
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + VAL_FIELD_NUMBER;
      hash = (53 * hash) + getVal();
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static org.wangyefeng.game.proto.Common.PbInt parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static org.wangyefeng.game.proto.Common.PbInt parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static org.wangyefeng.game.proto.Common.PbInt parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.wangyefeng.game.proto.Common.PbInt prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.wangyefeng.game.proto.PbInt}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.wangyefeng.game.proto.PbInt)
        org.wangyefeng.game.proto.Common.PbIntOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbInt_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbInt_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.wangyefeng.game.proto.Common.PbInt.class, org.wangyefeng.game.proto.Common.PbInt.Builder.class);
      }

      // Construct using org.wangyefeng.game.proto.Common.PbInt.newBuilder()
      private Builder() {

      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);

      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        val_ = 0;
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbInt_descriptor;
      }

      @java.lang.Override
      public org.wangyefeng.game.proto.Common.PbInt getDefaultInstanceForType() {
        return org.wangyefeng.game.proto.Common.PbInt.getDefaultInstance();
      }

      @java.lang.Override
      public org.wangyefeng.game.proto.Common.PbInt build() {
        org.wangyefeng.game.proto.Common.PbInt result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.wangyefeng.game.proto.Common.PbInt buildPartial() {
        org.wangyefeng.game.proto.Common.PbInt result = new org.wangyefeng.game.proto.Common.PbInt(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(org.wangyefeng.game.proto.Common.PbInt result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.val_ = val_;
        }
      }

      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.wangyefeng.game.proto.Common.PbInt) {
          return mergeFrom((org.wangyefeng.game.proto.Common.PbInt)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.wangyefeng.game.proto.Common.PbInt other) {
        if (other == org.wangyefeng.game.proto.Common.PbInt.getDefaultInstance()) return this;
        if (other.getVal() != 0) {
          setVal(other.getVal());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                val_ = input.readInt32();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private int val_ ;
      /**
       * <code>int32 val = 1;</code>
       * @return The val.
       */
      @java.lang.Override
      public int getVal() {
        return val_;
      }
      /**
       * <code>int32 val = 1;</code>
       * @param value The val to set.
       * @return This builder for chaining.
       */
      public Builder setVal(int value) {

        val_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>int32 val = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearVal() {
        bitField0_ = (bitField0_ & ~0x00000001);
        val_ = 0;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:org.wangyefeng.game.proto.PbInt)
    }

    // @@protoc_insertion_point(class_scope:org.wangyefeng.game.proto.PbInt)
    private static final org.wangyefeng.game.proto.Common.PbInt DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.wangyefeng.game.proto.Common.PbInt();
    }

    public static org.wangyefeng.game.proto.Common.PbInt getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<PbInt>
        PARSER = new com.google.protobuf.AbstractParser<PbInt>() {
      @java.lang.Override
      public PbInt parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<PbInt> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<PbInt> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.wangyefeng.game.proto.Common.PbInt getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface PbStringOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.wangyefeng.game.proto.PbString)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string val = 1;</code>
     * @return The val.
     */
    java.lang.String getVal();
    /**
     * <code>string val = 1;</code>
     * @return The bytes for val.
     */
    com.google.protobuf.ByteString
        getValBytes();
  }
  /**
   * Protobuf type {@code org.wangyefeng.game.proto.PbString}
   */
  public static final class PbString extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:org.wangyefeng.game.proto.PbString)
      PbStringOrBuilder {
  private static final long serialVersionUID = 0L;
    static {
      com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
        com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
        /* major= */ 4,
        /* minor= */ 27,
        /* patch= */ 1,
        /* suffix= */ "",
        PbString.class.getName());
    }
    // Use PbString.newBuilder() to construct.
    private PbString(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
    }
    private PbString() {
      val_ = "";
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbString_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbString_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.wangyefeng.game.proto.Common.PbString.class, org.wangyefeng.game.proto.Common.PbString.Builder.class);
    }

    public static final int VAL_FIELD_NUMBER = 1;
    @SuppressWarnings("serial")
    private volatile java.lang.Object val_ = "";
    /**
     * <code>string val = 1;</code>
     * @return The val.
     */
    @java.lang.Override
    public java.lang.String getVal() {
      java.lang.Object ref = val_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        val_ = s;
        return s;
      }
    }
    /**
     * <code>string val = 1;</code>
     * @return The bytes for val.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getValBytes() {
      java.lang.Object ref = val_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        val_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!com.google.protobuf.GeneratedMessage.isStringEmpty(val_)) {
        com.google.protobuf.GeneratedMessage.writeString(output, 1, val_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessage.isStringEmpty(val_)) {
        size += com.google.protobuf.GeneratedMessage.computeStringSize(1, val_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.wangyefeng.game.proto.Common.PbString)) {
        return super.equals(obj);
      }
      org.wangyefeng.game.proto.Common.PbString other = (org.wangyefeng.game.proto.Common.PbString) obj;

      if (!getVal()
          .equals(other.getVal())) return false;
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + VAL_FIELD_NUMBER;
      hash = (53 * hash) + getVal().hashCode();
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static org.wangyefeng.game.proto.Common.PbString parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static org.wangyefeng.game.proto.Common.PbString parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input);
    }
    public static org.wangyefeng.game.proto.Common.PbString parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessage
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.wangyefeng.game.proto.Common.PbString prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.wangyefeng.game.proto.PbString}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.wangyefeng.game.proto.PbString)
        org.wangyefeng.game.proto.Common.PbStringOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbString_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbString_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.wangyefeng.game.proto.Common.PbString.class, org.wangyefeng.game.proto.Common.PbString.Builder.class);
      }

      // Construct using org.wangyefeng.game.proto.Common.PbString.newBuilder()
      private Builder() {

      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);

      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        val_ = "";
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.wangyefeng.game.proto.Common.internal_static_org_wangyefeng_game_proto_PbString_descriptor;
      }

      @java.lang.Override
      public org.wangyefeng.game.proto.Common.PbString getDefaultInstanceForType() {
        return org.wangyefeng.game.proto.Common.PbString.getDefaultInstance();
      }

      @java.lang.Override
      public org.wangyefeng.game.proto.Common.PbString build() {
        org.wangyefeng.game.proto.Common.PbString result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.wangyefeng.game.proto.Common.PbString buildPartial() {
        org.wangyefeng.game.proto.Common.PbString result = new org.wangyefeng.game.proto.Common.PbString(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(org.wangyefeng.game.proto.Common.PbString result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.val_ = val_;
        }
      }

      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.wangyefeng.game.proto.Common.PbString) {
          return mergeFrom((org.wangyefeng.game.proto.Common.PbString)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.wangyefeng.game.proto.Common.PbString other) {
        if (other == org.wangyefeng.game.proto.Common.PbString.getDefaultInstance()) return this;
        if (!other.getVal().isEmpty()) {
          val_ = other.val_;
          bitField0_ |= 0x00000001;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 10: {
                val_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000001;
                break;
              } // case 10
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private java.lang.Object val_ = "";
      /**
       * <code>string val = 1;</code>
       * @return The val.
       */
      public java.lang.String getVal() {
        java.lang.Object ref = val_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          val_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string val = 1;</code>
       * @return The bytes for val.
       */
      public com.google.protobuf.ByteString
          getValBytes() {
        java.lang.Object ref = val_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          val_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string val = 1;</code>
       * @param value The val to set.
       * @return This builder for chaining.
       */
      public Builder setVal(
          java.lang.String value) {
        if (value == null) { throw new NullPointerException(); }
        val_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>string val = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearVal() {
        val_ = getDefaultInstance().getVal();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>string val = 1;</code>
       * @param value The bytes for val to set.
       * @return This builder for chaining.
       */
      public Builder setValBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        checkByteStringIsUtf8(value);
        val_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:org.wangyefeng.game.proto.PbString)
    }

    // @@protoc_insertion_point(class_scope:org.wangyefeng.game.proto.PbString)
    private static final org.wangyefeng.game.proto.Common.PbString DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.wangyefeng.game.proto.Common.PbString();
    }

    public static org.wangyefeng.game.proto.Common.PbString getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<PbString>
        PARSER = new com.google.protobuf.AbstractParser<PbString>() {
      @java.lang.Override
      public PbString parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<PbString> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<PbString> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.wangyefeng.game.proto.Common.PbString getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_wangyefeng_game_proto_PbInt_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_org_wangyefeng_game_proto_PbInt_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_wangyefeng_game_proto_PbString_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_org_wangyefeng_game_proto_PbString_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\014common.proto\022\031org.wangyefeng.game.prot" +
      "o\"\024\n\005PbInt\022\013\n\003val\030\001 \001(\005\"\027\n\010PbString\022\013\n\003v" +
      "al\030\001 \001(\tb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_org_wangyefeng_game_proto_PbInt_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_wangyefeng_game_proto_PbInt_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_org_wangyefeng_game_proto_PbInt_descriptor,
        new java.lang.String[] { "Val", });
    internal_static_org_wangyefeng_game_proto_PbString_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_wangyefeng_game_proto_PbString_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_org_wangyefeng_game_proto_PbString_descriptor,
        new java.lang.String[] { "Val", });
    descriptor.resolveAllFeaturesImmutable();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
