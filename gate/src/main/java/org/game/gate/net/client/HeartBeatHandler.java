package org.game.gate.net.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.game.proto.DecoderType;
import org.game.proto.Topic;
import org.game.proto.protocol.GateToLogicProtocol;

@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    private static final ByteBuf PING = Unpooled.unreleasableBuffer(Unpooled.directBuffer(8));

    private final Client client;

    static {
        PING.writeInt(4);
        PING.writeByte(DecoderType.MESSAGE_CODE.getCode());
        PING.writeByte(Topic.GATE.getCode());
        PING.writeShort(GateToLogicProtocol.PING.getCode());
    }

    public HeartBeatHandler(Client client) {
        this.client = client;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();
            if (state == IdleState.READER_IDLE) {
                log.warn("读空闲，断开连接！！！连接: {}", client);
                ctx.channel().close();
            } else if (state == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(PING.duplicate());
                log.debug("写空闲，发送心跳包！！！连接: {}", client);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
