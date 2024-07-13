package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

/**
 * 消息码解码器
 */
public class MessagePlayerDecoder implements Decoder<MessagePlayer<?>> {

    public MessagePlayerDecoder() {
    }

    @Override
    public MessagePlayer<?> decode(ByteBuf msg, byte to) throws Exception {
        byte from = msg.readByte();
        short code = msg.readShort();
        int playerId = msg.readInt();
        int length = msg.readableBytes();
        Protocol protocol = ProtocolUtils.getProtocol(from, to, code);
        if (length > 0) {
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
