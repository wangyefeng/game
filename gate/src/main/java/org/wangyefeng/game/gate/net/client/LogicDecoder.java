package org.wangyefeng.game.gate.net.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.wangyefeng.game.gate.net.GateMessage;
import org.wangyefeng.game.gate.protocol.C2SProtocol;

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
                int code = in.readShort();
                Assert.isTrue(C2SProtocol.match(code), "Invalid code: " + code);
                int length = in.readableBytes();
                if (length > 0) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(in);
                    com.google.protobuf.Message message = (com.google.protobuf.Message) C2SProtocol.getParser(code).parseFrom(inputStream);
                    out.add(new GateMessage<>(code, message));
                } else {
                    out.add(new GateMessage<>(code));
                }
            }
        }
    }
}
