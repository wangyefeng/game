package org.wangyefeng.game.logic.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 客户端消息编码器
 */
@ChannelHandler.Sharable
public class ClientMsgEncode extends MessageToByteEncoder<ClientMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ClientMessage msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);// 协议长度占位
            out.writeByte(ProtocolType.LOGIC_CLIENT.getValue());// 协议类型
            out.writeShort(msg.getCode());// 协议号
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);// protobuf
            out.writeInt(msg.getPlayerId());// 玩家ID
            out.setInt(0, out.readableBytes() - TcpServer.FRAME_LENGTH);// 协议长度，写入包体头部
        } else {
            out.writeInt(TcpServer.PROTOCOL_TYPE_LENGTH + TcpServer.CODE_LENGTH + TcpServer.PLAYER_ID_LENGTH);
            out.writeByte(0);// 协议类型
            out.writeShort(msg.getCode());// 协议号
            out.writeInt(msg.getPlayerId());// 玩家ID
        }
    }
}
