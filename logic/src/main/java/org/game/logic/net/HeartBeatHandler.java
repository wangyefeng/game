package org.game.logic.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import jakarta.annotation.Nonnull;
import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.game.proto.DecoderType;
import org.game.proto.Topic;
import org.game.proto.protocol.LogicToGateProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

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
    public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().attr(ChannelKeys.PLAYERS_KEY).set(new Vector<>());
        log.info("client channel active: {}", ctx.channel().remoteAddress());
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
//                    ctx.channel().writeAndFlush(PING.duplicate());
                    log.debug("写空闲，发送心跳包！！！连接: {}", ctx.channel().remoteAddress());
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        List<Integer> players = ctx.channel().attr(ChannelKeys.PLAYERS_KEY).get();
        for (Integer playerId : players) {
            Player player = Players.getPlayer(playerId);
            if (player == null) {
                continue;
            }
            player.execute(() -> {
                if (player.isOnline()) {
                    player.logout();
                }
            });
        }
        log.info("与客户端连接断开, 地址：{}", ctx.channel().remoteAddress());
    }
}
