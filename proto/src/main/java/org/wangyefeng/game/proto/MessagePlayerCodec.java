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
public class MessagePlayerCodec extends ByteToMessageCodec<MessagePlayer<?>> {

    private final ProtocolInMatcher matcher;

    public MessagePlayerCodec(ProtocolInMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePlayer<?> msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);// 协议长度占位
            out.writeInt(msg.getPlayerId());// 玩家ID
            out.writeShort(msg.getCode());// 协议号
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);// protobuf
            out.setInt(0, out.readableBytes() - 4);// 协议长度，写入包体头部
        } else {
            out.writeInt(6);
            out.writeShort(msg.getCode());// 协议号
            out.writeInt(msg.getPlayerId());// 玩家ID
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            short code = in.readShort();
            Assert.isTrue(matcher.match(code), "Invalid code: " + code);
            int playerId = in.readInt();
            int length = in.readableBytes();
            if (length > 0) {
                ByteBufInputStream inputStream = new ByteBufInputStream(in);
                Message message = (Message) matcher.parser(code).parseFrom(inputStream);
                out.add(new MessagePlayer<>(playerId, code, message));
            } else {
                out.add(new MessagePlayer<>(playerId, code));
            }
        } finally {
            in.skipBytes(in.readableBytes());
        }
    }
}
