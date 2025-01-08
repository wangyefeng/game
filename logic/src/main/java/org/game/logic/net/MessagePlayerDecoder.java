package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import jakarta.transaction.NotSupportedException;
import org.game.proto.Decoder;
import org.game.proto.DecoderType;
import org.game.proto.MessagePlayer;
import org.game.proto.Topic;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;

/**
 * 消息码解码器
 */
public class MessagePlayerDecoder implements Decoder {

    public MessagePlayerDecoder() {
    }

    @Override
    public Object decode(ByteBuf msg, byte to) throws Exception {
        byte from = msg.readByte();
        int playerId = msg.readInt();
        short code = msg.readShort();
        Protocol protocol = Protocols.getProtocol(from, to, code);
        if (protocol == null) {
            throw new UnsupportedOperationException("协议不存在：" + from + "->" + to + ":" + code);
        }
        MessagePlayer<?> messagePlayer;
        if (protocol.parser() != null) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            Message message = (Message) protocol.parser().parseFrom(inputStream);
            messagePlayer = new MessagePlayer<>(playerId, protocol, message);
        } else {
            messagePlayer = new MessagePlayer<>(playerId, protocol);
        }
        if (protocol.from().equals(Topic.CLIENT)) {
            return new ClientMessage<>(messagePlayer);
        } else if (protocol.from().equals(Topic.GATE)) {
            return new GateMessage<>(messagePlayer);
        } else {
            throw new NotSupportedException("Unsupported topic: " + protocol.from());
        }
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_PLAYER;
    }
}
