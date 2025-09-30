package org.wyf.game.logic.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.wyf.game.proto.DecoderType;
import org.wyf.game.proto.Topic;
import org.wyf.game.proto.protocol.LogicToGateProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    private static final ByteBuf PING = Unpooled.unreleasableBuffer(Unpooled.directBuffer(8));

    static {
        PING.writeInt(4);
        PING.writeByte(DecoderType.MESSAGE_CODE.getCode());
        PING.writeByte(Topic.GATE.getCode());
        PING.writeShort(LogicToGateProtocol.PING.getCode());
    }

    public HeartBeatHandler() {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();
            switch (state) {
                case READER_IDLE, ALL_IDLE -> {
                    log.warn("读空闲，断开连接！！！连接: {}", ctx.channel().remoteAddress());
                    ctx.channel().close();
                }
                case WRITER_IDLE -> {
                    ctx.channel().writeAndFlush(PING.duplicate());
                    log.debug("写空闲，发送心跳包！！！连接: {}", ctx.channel().remoteAddress());
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
