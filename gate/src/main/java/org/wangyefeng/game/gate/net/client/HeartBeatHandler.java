package org.wangyefeng.game.gate.net.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.protocol.C2SProtocol;

@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    private static final ByteBuf PING = Unpooled.unreleasableBuffer(Unpooled.directBuffer(7));

    private final Client client;

    static {
        PING.writeInt(3);
        PING.writeByte(1);
        PING.writeShort(C2SProtocol.PING.getCode());
    }

    public HeartBeatHandler(Client client) {
        this.client = client;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();
            if (state == IdleState.READER_IDLE) {
                log.warn("读空闲，断开连接！！！连接: {}", ctx.channel());
                ctx.close();
                client.setRunning(false);
                client.connect();
            } else if (state == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(PING.duplicate());
                log.info("写空闲，发送心跳包！！！连接: {}", ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
