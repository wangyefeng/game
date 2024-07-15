package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.springframework.util.Assert;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.Protocols;

/**
 * 消息码解码器
 */
public class MessageCodeDecoder implements Decoder<MessageCode<?>> {

    public MessageCodeDecoder() {
    }

    @Override
    public MessageCode<?> decode(ByteBuf msg, byte to) throws Exception {
        byte from = msg.readByte();
        short code = msg.readShort();
        int length = msg.readableBytes();
        Protocol protocol = Protocols.getProtocol(from, to, code);
        Assert.notNull(protocol, "No protocol found for from: " + from + ", to: " + to + ", code: " + code);
        if (length > 0) {
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
