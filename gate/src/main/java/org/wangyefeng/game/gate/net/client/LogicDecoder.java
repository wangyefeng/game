package org.wangyefeng.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.wangyefeng.game.gate.protocol.LogicProtocol;
import org.wangyefeng.game.proto.MessageCode;

import java.util.List;

public class LogicDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(LogicDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();// 协议类型
        switch (type) {
            case 0 -> {
                // 转发消息
                log.info("转发消息协议");
                in.markReaderIndex();
                in.skipBytes(in.readableBytes() - 4);
                int playerId = in.readInt(); // 玩家id
                in.resetReaderIndex();
//                ctx.channel().writeAndFlush(msg.readerIndex(msg.readableBytes() - 4).retain());
            }
            case 1 -> { // 网关处理消息
                short code = in.readShort();
                Assert.isTrue(LogicProtocol.match(code), "Invalid code: " + code);
                int length = in.readableBytes();
                if (length > 0) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(in);
                    Message message = (Message) LogicProtocol.getParser(code).parseFrom(inputStream);
                    out.add(new MessageCode<>(code, message));
                } else {
                    out.add(new MessageCode<>(code));
                }
            }
        }
    }
}
