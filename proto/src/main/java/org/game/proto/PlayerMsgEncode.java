package org.game.proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.game.proto.protocol.Protocol;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 客户端消息编码器
 */
@ChannelHandler.Sharable
public class PlayerMsgEncode extends MessageToByteEncoder<MessagePlayer<?>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePlayer<?> msg, ByteBuf out) throws Exception {
        if (msg.getData() != null) {
            out.writeInt(0);// 协议长度，占位
            out.writeByte(DecoderType.MESSAGE_PLAYER.getCode());// 协议类型
            out.writeByte(msg.getProtocol().to().getCode());
            out.writeInt(msg.getPlayerId());// 玩家ID
            out.writeShort(msg.getCode());// 协议号
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getData().writeTo(outputStream);// protobuf
            out.setInt(0, out.readableBytes() - Protocol.FRAME_LENGTH);// 协议长度，写入包体头部
        } else {
            out.writeInt(8);
            out.writeByte(DecoderType.MESSAGE_PLAYER.getCode());// 协议类型
            out.writeByte(msg.getProtocol().to().getCode());
            out.writeInt(msg.getPlayerId());// 玩家ID
            out.writeShort(msg.getCode());// 协议号
        }
    }
}
