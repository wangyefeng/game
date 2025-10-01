package org.wyf.game.proto;

import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import jakarta.annotation.Nonnull;
import org.wyf.game.proto.protocol.Protocol;
import org.wyf.game.proto.util.ProtobufJsonUtil;

import java.util.Objects;

public record MessageCode<T extends Message>(Protocol protocol, T data) {

    public MessageCode(Protocol protocol, T data) {
        this.protocol = Objects.requireNonNull(protocol);
        this.data = Objects.requireNonNull(data);
    }

    public static <T extends Message> MessageCode<T> of(@Nonnull Protocol protocol, @Nonnull T data) {
        return new MessageCode<>(protocol, data);
    }

    public static MessageCode<Empty> of(@Nonnull Protocol protocol) {
        return new MessageCode<>(protocol, Empty.getDefaultInstance());
    }

    @Override
    public @Nonnull T data() {
        return data;
    }

    @Override
    public @Nonnull Protocol protocol() {
        return protocol;
    }

    public short getCode() {
        return protocol.getCode();
    }

    @Override
    public String toString() {
        return "protocol=" +
                protocol.getClass().getSimpleName() +
                "." +
                protocol +
                ", data=" + ProtobufJsonUtil.serializeMessage(data);
    }

}
