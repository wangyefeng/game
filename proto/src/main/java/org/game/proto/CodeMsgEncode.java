package org.game.proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.game.proto.protocol.Protocol;

/**
 * 客户端消息编码器
 *
 * @author wangyefeng
 * @date 2024-07-08
 */
@ChannelHandler.Sharable
public class CodeMsgEncode extends MessageToByteEncoder<MessageCode<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageCode<?> msg, ByteBuf out) throws Exception {
        out.writeInt(0);// 协议长度，占位
        out.writeByte(DecoderType.MESSAGE_CODE.getCode());// 协议类型
        out.writeByte(msg.getProtocol().to().getCode());
        out.writeShort(msg.getCode());// 协议号
        ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
        msg.getData().writeTo(outputStream);// protobuf
        out.setInt(0, out.readableBytes() - Protocol.FRAME_LENGTH);// 协议长度，写入包体头部
    }
}
