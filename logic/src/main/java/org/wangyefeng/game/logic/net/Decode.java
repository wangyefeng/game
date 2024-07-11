package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.util.Assert;
import org.wangyefeng.game.logic.protocol.ClientProtocol;
import org.wangyefeng.game.logic.protocol.GateProtocol;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.MessagePlayer;

import java.util.List;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 解码器
 */
public class Decode extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            int type = in.readByte();
            if (type == ProtocolType.LOGIC_CLIENT.getValue()) { // 客户端消息
                decodeClient(in, out);
            } else if (type == ProtocolType.LOGIC_GATE.getValue()) { // 网关消息
                decodeGate(in, out);
            } else {
                throw new Exception("Invalid message type: " + type);
            }
        } finally {
            in.skipBytes(in.readableBytes());
        }
    }

    /**
     * 客户端消息解码, code(2bytes) + message(protobuf) + playerId(4bytes)
     * @throws Exception
     */
    private void decodeClient(ByteBuf in, List<Object> out) throws Exception {
        short code = in.readShort();
        Assert.isTrue(ClientProtocol.match(code), "Invalid client code: " + code);
        int playerId = in.readInt();
        int length = in.readableBytes();
        if (length > 0) {
            ByteBufInputStream inputStream = new ByteBufInputStream(in);
            Message message = (Message) ClientProtocol.getParser(code).parseFrom(inputStream);
            out.add(new MessagePlayer<>(playerId, code, message));
        } else {
            out.add(new MessagePlayer<>(playerId, code));
        }
    }

    /**
     * 网关消息解码, code(2bytes) + message(protobuf)
     * @throws Exception
     */
    private void decodeGate(ByteBuf in, List<Object> out) throws Exception {
        short code = in.readShort();
        Assert.isTrue(GateProtocol.match(code), "Invalid gate code: " + code);
        int length = in.readableBytes();
        if (length > 0) {
            ByteBufInputStream inputStream = new ByteBufInputStream(in);
            Message message = (Message) GateProtocol.getParser(code).parseFrom(inputStream);
            out.add(new MessageCode<>(code, message));
        } else {
            out.add(new MessageCode<>(code));
        }
    }
}
