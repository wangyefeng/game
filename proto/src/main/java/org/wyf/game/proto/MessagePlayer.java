package org.wyf.game.proto;

import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import jakarta.annotation.Nonnull;
import org.wyf.game.proto.protocol.Protocol;
import org.wyf.game.proto.util.ProtobufJsonUtil;

import java.util.Objects;

public final class MessagePlayer<T extends Message> {

    private final int playerId;

    private final Protocol protocol;

    private final T data;

    private MessagePlayer(int playerId, Protocol protocol, T message) {
        this.playerId = playerId;
        this.protocol = Objects.requireNonNull(protocol);
        this.data = Objects.requireNonNull(message);
    }

    public static <T extends Message> MessagePlayer<T> of(int playerId, @Nonnull Protocol protocol, @Nonnull T message) {
        return new MessagePlayer<>(playerId, protocol, message);
    }

    public static MessagePlayer<Empty> of(int playerId, @Nonnull Protocol protocol) {
        return new MessagePlayer<>(playerId, protocol, Empty.getDefaultInstance());
    }

    public short getCode() {
        return protocol.getCode();
    }

    public @Nonnull T getData() {
        return data;
    }

    public int getPlayerId() {
        return playerId;
    }

    public @Nonnull Protocol getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return "playerId=" +
                playerId +
                ", protocol=" +
                protocol.getClass().getSimpleName() +
                "." +
                protocol + ", data=" + ProtobufJsonUtil.serializeMessage(data);
    }
}
