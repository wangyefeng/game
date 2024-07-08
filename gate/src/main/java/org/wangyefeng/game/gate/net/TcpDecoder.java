package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.wangyefeng.game.gate.net.client.LogicClient;
import org.wangyefeng.game.gate.protocol.LogicProtocol;

import java.net.SocketException;
import java.util.List;

public class TcpDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(TcpDecoder.class);


    private static final byte GATE = (byte) 1;


    private static final byte LOGIC = (byte) 2;


    private static final byte CROSS = (byte) 3;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Channel active: {}", ctx.channel());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte tag = msg.readByte();// 发送服务器tag
        switch (tag) {
            case GATE -> {
                int code = msg.readShort();
                Assert.isTrue(LogicProtocol.match(code), "Invalid code: " + code);
                int length = msg.readableBytes();
                if (length > 0) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(msg);
                    out.add(new GateMessage<>(code, (Message) LogicProtocol.getParser(code).parseFrom(inputStream)));
                } else {
                    out.add(new GateMessage<>(code));
                }
            }
            case LOGIC -> {
                LogicClient client = LogicClient.getInstance();
                if (client.isRunning()) {
                    msg.writeInt(1);
                    client.getChannel().writeAndFlush(msg.retain());
                } else {
                    log.error("Logic client not running, discard message: {}", msg);
                }
            }
            case CROSS -> {
                // TODO: 处理跨服消息
                log.warn("Cross server message not implemented yet: {}", msg);
            }
            default -> log.warn("Unknown tag: {}", tag);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            log.info("Socket exception {} channel: {}", cause.getMessage(), ctx.channel());
        } else if (cause instanceof ReadTimeoutException) {
            log.info("Read timeout: {}", ctx.channel());
        } else {
            log.error("Exception caught in channel: {}", ctx.channel(), cause);
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("Channel inactive: {}", ctx.channel());
    }
}
