package org.game.logic.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Nonnull;
import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.game.proto.CodeMsgHandler;
import org.game.proto.MessageCode;
import org.game.proto.MessagePlayer;
import org.game.proto.MsgHandler;
import org.game.proto.PlayerMsgHandler;
import org.game.proto.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Vector;

/**
 * 处理消息的handler
 *
 * @author wangyefeng
 */
@ChannelHandler.Sharable
public class TcpHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(TcpHandler.class);

    @Override
    public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().attr(ChannelKeys.PLAYERS_KEY).set(new Vector<>());
        log.info("client channel active: {}", ctx.channel().remoteAddress());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws OperationNotSupportedException {
        if (message instanceof MessagePlayer<?> messagePlayer) {
            MsgHandler handler = MsgHandler.getHandler(messagePlayer.getProtocol());
            if (handler == null) {
                log.error("协议未定义处理器：{}", Protocol.toString(messagePlayer.getProtocol()));
                return;
            }
            if (!(handler instanceof PlayerMsgHandler playerMsgHandler)) {
                log.error("协议处理器类型不匹配：{}", Protocol.toString(messagePlayer.getProtocol()));
                return;
            }
            try {
                ((PlayerMsgHandler<Message>) playerMsgHandler).handle(ctx.channel(), messagePlayer.getPlayerId(), messagePlayer.getData());
            } catch (Exception e) {
                log.error("协议处理失败 message：{}", message, e);
            }
        } else if (message instanceof MessageCode<?> messageCode) {
            MsgHandler handler = MsgHandler.getHandler(messageCode.getProtocol());
            if (handler == null) {
                log.error("协议未定义处理器：{}", Protocol.toString(messageCode.getProtocol()));
                return;
            }
            if (!(handler instanceof CodeMsgHandler codeMsgHandler)) {
                log.error("协议处理器类型不匹配：{}", Protocol.toString(messageCode.getProtocol()));
                return;
            }
            try {
                codeMsgHandler.handle(ctx.channel(), messageCode.getData());
            } catch (Exception e) {
                log.error("协议处理失败 message：{}", message, e);
            }
        } else {
            throw new OperationNotSupportedException("不支持的消息类型");
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
                    Players.removePlayer(playerId);
                    log.info("玩家{}退出游戏", playerId);
                }
            });
        }
        log.info("与客户端连接断开, 地址：{}", ctx.channel().remoteAddress());
    }

}
