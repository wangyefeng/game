package org.wangyefeng.game.logic.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.springframework.util.Assert;
import org.wangyefeng.game.logic.protocol.Gate2LogicProtocol;

import java.util.List;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 编解码器
 */
public class GateCodec extends ByteToMessageCodec<Client2ServerMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Client2ServerMessage msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);// 协议长度占位
            out.writeByte(0);// 协议类型
            out.writeShort(msg.getCode());// 协议号
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);// protobuf
            out.writeInt(msg.getPlayerId());// 玩家ID
            out.setInt(0, out.readableBytes() - 4);// 协议长度，写入包体头部
        } else {
            out.writeInt(7);// 协议长度 1 + 2 + 4
            out.writeByte(0);// 协议类型
            out.writeShort(msg.getCode());// 协议号
            out.writeInt(msg.getPlayerId());// 玩家ID
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int type = in.readByte();
        switch (type) {
            case 0:// 客户端解码
                decode0(in, out);
                break;
            case 1:// 网关解码
                decode1(in, out);
                break;
            default:
                throw new Exception("Invalid message type: " + type);
        }
    }

    private void decode0(ByteBuf in, List<Object> out) throws Exception {
        int code = in.readShort();
        Assert.isTrue(Gate2LogicProtocol.match(code), "Invalid code: " + code);
        int length = in.readableBytes();
        if (length > 4) {
            ByteBufInputStream inputStream = new ByteBufInputStream(in, length - 4);
            com.google.protobuf.Message message = (com.google.protobuf.Message) Gate2LogicProtocol.getParser(code).parseFrom(inputStream);
            int playerId = in.readInt();
            out.add(new Client2ServerMessage<>(playerId, code, message));
        } else {
            int playerId = in.readInt();
            out.add(new Client2ServerMessage<>(playerId, code));
        }
    }

    private void decode1(ByteBuf in, List<Object> out) throws Exception {
        int code = in.readShort();
        Assert.isTrue(Gate2LogicProtocol.match(code), "Invalid code: " + code);
        int length = in.readableBytes();
        if (length > 4) {
            ByteBufInputStream inputStream = new ByteBufInputStream(in);
            com.google.protobuf.Message message = (com.google.protobuf.Message) Gate2LogicProtocol.getParser(code).parseFrom(inputStream);
            out.add(new Gate2ServerMessage<>(code, message));
        } else {
            out.add(new Gate2ServerMessage<>(code));
        }
    }
}
