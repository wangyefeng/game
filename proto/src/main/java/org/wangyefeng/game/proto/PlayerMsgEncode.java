package org.wangyefeng.game.proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.wangyefeng.game.proto.protocol.Protocol;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 客户端消息编码器
 */
@ChannelHandler.Sharable
public class PlayerMsgEncode extends MessageToByteEncoder<MessagePlayer> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePlayer msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);// 协议长度占位
            out.writeByte(DecoderType.MESSAGE_PLAYER.getCode());// 协议类型
            out.writeByte(msg.getProtocol().to().getCode());
            out.writeShort(msg.getCode());// 协议号
            out.writeInt(msg.getPlayerId());// 玩家ID
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);// protobuf
            out.setInt(0, out.readableBytes() - Protocol.FRAME_LENGTH);// 协议长度，写入包体头部
        } else {
            out.writeInt(8);
            out.writeByte(DecoderType.MESSAGE_PLAYER.getCode());// 协议类型
            out.writeByte(msg.getProtocol().to().getCode());
            out.writeShort(msg.getCode());// 协议号
            out.writeInt(msg.getPlayerId());// 玩家ID
        }
    }
}
