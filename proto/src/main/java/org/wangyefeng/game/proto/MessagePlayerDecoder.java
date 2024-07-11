package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.springframework.util.Assert;

/**
 * 消息码解码器
 */
public class MessagePlayerDecoder implements Decoder<MessagePlayer<?>> {


    private final ProtocolInMatcher matcher;

    public MessagePlayerDecoder(ProtocolInMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public MessagePlayer<?> decode(ByteBuf msg) throws Exception {
        short code = msg.readShort();
        Assert.isTrue(matcher.match(code), "Invalid code: " + code);
        int playerId = msg.readInt();
        int length = msg.readableBytes();
        if (length > 0) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            Message message = (Message) matcher.parser(code).parseFrom(inputStream);
            return new MessagePlayer<>(playerId, code, message);
        } else {
            return new MessagePlayer<>(playerId, code);
        }
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_PLAYER;
    }
}
