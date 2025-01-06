package org.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.springframework.util.Assert;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;

/**
 * 消息码解码器
 */
public class MessageCodeDecoder implements Decoder {

    public MessageCodeDecoder() {
    }

    @Override
    public Object decode(ByteBuf msg, byte to) throws Exception {
        byte from = msg.readByte();
        short code = msg.readShort();
        Protocol protocol = Protocols.getProtocol(from, to, code);
        Assert.notNull(protocol, "No protocol found for from: " + from + ", to: " + to + ", code: " + code);
        if (protocol.parser() != null) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            Message message = (Message) protocol.parser().parseFrom(inputStream);
            return new MessageCode<>(protocol, message);
        } else {
            return new MessageCode<>(protocol);
        }
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_CODE;
    }
}
