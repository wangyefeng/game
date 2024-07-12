package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

import java.util.List;

/**
 * 解码器
 *
 * @author wangyefeng
 * @date 2024-07-11
 */
public class MessagePlayerCodec extends ByteToMessageCodec<MessagePlayer<?>> {

    private final byte to;

    public MessagePlayerCodec(byte to) {
        this.to = to;
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
            byte from = in.readByte();
            short code = in.readShort();
            int playerId = in.readInt();
            int length = in.readableBytes();
            Protocol protocol = ProtocolUtils.getProtocol(from, to, code);
            if (length > 0) {
                ByteBufInputStream inputStream = new ByteBufInputStream(in);
                Message message = (Message) protocol.parser().parseFrom(inputStream);
                out.add(new MessagePlayer<>(playerId, protocol, message));
            } else {
                out.add(new MessagePlayer<>(playerId, protocol));
            }
        } finally {
            in.skipBytes(in.readableBytes());
        }
    }
}
