package org.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;

/**
 * 消息码解码器
 */
public class MessagePlayerDecoder implements Decoder<MessagePlayer<?>> {

    public MessagePlayerDecoder() {
    }

    @Override
    public MessagePlayer<?> decode(ByteBuf msg, byte to) throws Exception {
        byte from = msg.readByte();
        int playerId = msg.readInt();
        short code = msg.readShort();
        Protocol protocol = Protocols.getProtocol(from, to, code);
        if (protocol.parser() != null) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            Message message = (Message) protocol.parser().parseFrom(inputStream);
            return new MessagePlayer<>(playerId, protocol, message);
        } else {
            return new MessagePlayer<>(playerId, protocol);
        }
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_PLAYER;
    }
}
