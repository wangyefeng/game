package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.springframework.util.Assert;

/**
 * 消息码解码器
 */
public class MessageCodeDecoder implements Decoder<MessageCode<?>> {


    private final ProtocolInMatcher matcher;

    public MessageCodeDecoder(ProtocolInMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public MessageCode<?> decode(ByteBuf msg) throws Exception {
        short code = msg.readShort();
        Assert.isTrue(matcher.match(code), "Invalid code: " + code);
        int length = msg.readableBytes();
        if (length > 0) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            Message message = (Message) matcher.parser(code).parseFrom(inputStream);
            return new MessageCode<>(code, message);
        } else {
            return new MessageCode<>(code);
        }
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_CODE;
    }
}
