package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

/**
 * 消息码解码器
 */
public class MessageCodeDecoder implements Decoder<MessageCode<?>> {

    private final byte to;

    public MessageCodeDecoder(byte to) {
        this.to = to;
    }

    @Override
    public MessageCode<?> decode(ByteBuf msg) throws Exception {
        byte from = msg.readByte();
        short code = msg.readShort();
        int length = msg.readableBytes();
        Protocol protocol = ProtocolUtils.getProtocol(from, to, code);
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
