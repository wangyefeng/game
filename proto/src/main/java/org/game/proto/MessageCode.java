package org.game.proto;

import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import jakarta.annotation.Nonnull;
import org.game.proto.protocol.Protocol;
import org.game.proto.util.ProtobufJsonUtil;

import java.util.Objects;

public final class MessageCode<T extends Message> {

    private final Protocol protocol;

    private final T data;

    private MessageCode(Protocol protocol, T data) {
        this.protocol = Objects.requireNonNull(protocol);
        this.data = Objects.requireNonNull(data);
    }

    public static <T extends Message> MessageCode<T> of(@Nonnull Protocol protocol, @Nonnull T data) {
        return new MessageCode<>(protocol, data);
    }

    public static MessageCode<Empty> of(@Nonnull Protocol protocol) {
        return new MessageCode<>(protocol, Empty.getDefaultInstance());
    }

    public @Nonnull T getData() {
        return data;
    }

    public @Nonnull Protocol getProtocol() {
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
