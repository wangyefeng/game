package org.game.gate.net.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.game.proto.DecoderType;
import org.game.proto.MessageCode;

import static org.game.proto.protocol.Protocol.FRAME_LENGTH;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 客户端消息编码器
 */
@ChannelHandler.Sharable
public class GateClientEncode extends MessageToByteEncoder<MessageCode<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageCode<?> msg, ByteBuf out) throws Exception {
        if (msg.getData() != null) {
            out.writeInt(0);// 协议长度占位
            out.writeByte(DecoderType.MESSAGE_CODE.getCode());// 协议类型
            out.writeByte(msg.getProtocol().from().getCode());
            out.writeShort(msg.getCode());// 协议号
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getData().writeTo(outputStream);// protobuf
            out.setInt(0, out.readableBytes() - FRAME_LENGTH);// 协议长度，写入包体头部
        } else {
            out.writeInt(4);
            out.writeByte(DecoderType.MESSAGE_CODE.getCode());// 协议类型
            out.writeByte(msg.getProtocol().from().getCode());
            out.writeShort(msg.getCode());// 协议号
        }
    }
}
