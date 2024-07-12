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
public class MessageCodeCodec extends ByteToMessageCodec<MessageCode<?>> {


    public MessageCodeCodec() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageCode<?> msg, ByteBuf out) throws Exception {
        Protocol protocol = msg.getProtocol();
        if (msg.getMessage() != null) {
            out.writeInt(0);// 包体长度占位
            out.writeByte(protocol.from().getCode());
            out.writeByte(1);
            out.writeShort(protocol.getCode());
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);
            out.setInt(0, out.readableBytes() - 4);
        } else {
            out.writeInt(4);
            out.writeByte(protocol.from().getCode());
            out.writeByte(1);
            out.writeShort(protocol.getCode());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            byte from = in.readByte();
            short code = in.readShort();
            Protocol protocol = ProtocolUtils.getProtocol(from, code);
            int length = in.readableBytes();
            if (length > 0) {
                ByteBufInputStream inputStream = new ByteBufInputStream(in);
                Message message = (Message) protocol.parser().parseFrom(inputStream);
                out.add(new MessageCode<>(protocol, message));
            } else {
                out.add(new MessageCode<>(protocol));
            }
        } finally {
            in.skipBytes(in.readableBytes());
        }
    }
}
