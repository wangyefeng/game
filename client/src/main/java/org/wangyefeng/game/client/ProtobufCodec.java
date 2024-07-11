package org.wangyefeng.game.client;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.springframework.util.Assert;
import org.wangyefeng.game.proto.MessageCode;

import java.util.List;

/**
 * Protobuf编解码器
 *
 * @author 王叶峰
 * @date 2021年6月25日
 */
public class ProtobufCodec extends ByteToMessageCodec<MessageCode> {


    @Override
    protected void encode(ChannelHandlerContext ctx, MessageCode msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);
            out.writeShort(msg.getCode());
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);
            out.setInt(0, out.readableBytes() - 4);
        } else {
            out.writeInt(2);
            out.writeShort(msg.getCode());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, List<Object> out) throws Exception {
        byte type = msg.readByte();
        short code = msg.readShort();
        Assert.isTrue(S2CProtocol.match(code), "Invalid code: " + code);
        int length = msg.readableBytes();
        if (length > 0) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            out.add(new MessageCode<>(code, (Message) S2CProtocol.getParser(code).parseFrom(inputStream)));
        } else {
            out.add(new MessageCode<>(code));
        }
    }

}
