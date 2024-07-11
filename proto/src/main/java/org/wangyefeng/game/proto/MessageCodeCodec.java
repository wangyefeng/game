package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 解码器
 *
 * @author wangyefeng
 * @date 2024-07-11
 */
public class MessageCodeCodec extends ByteToMessageCodec<MessageCode<?>> {

    private final ProtocolInMatcher matcher;

    public MessageCodeCodec(ProtocolInMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageCode<?> msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);// 包体长度占位
            out.writeShort(msg.getCode());
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);
            out.setInt(0, out.readableBytes() - 4);
        } else {
            out.writeInt(2);
            out.writeShort(msg.getCode());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            short code = in.readShort();
            Assert.isTrue(matcher.match(code), "Invalid code: " + code);
            int length = in.readableBytes();
            if (length > 0) {
                ByteBufInputStream inputStream = new ByteBufInputStream(in);
                Message message = (Message) matcher.parser(code).parseFrom(inputStream);
                out.add(new MessageCode<>(code, message));
            } else {
                out.add(new MessageCode<>(code));
            }
        } finally {
            in.skipBytes(in.readableBytes());
        }
    }
}
