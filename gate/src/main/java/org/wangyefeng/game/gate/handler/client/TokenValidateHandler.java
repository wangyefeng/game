package org.wangyefeng.game.gate.handler.client;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.gate.net.AttributeKeys;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.gate.player.Players;
import org.wangyefeng.game.gate.protocol.ClientProtocol;
import org.wangyefeng.game.gate.protocol.ToClientProtocol;
import org.wangyefeng.game.gate.thread.ThreadPool;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.struct.Common;

import java.util.concurrent.ExecutorService;

@Component
public class TokenValidateHandler implements ClientMsgHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(TokenValidateHandler.class);

    @Override
    public void handle(Channel channel, Common.PbInt msg) throws Exception {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("Player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }
        int playerId = msg.getVal();
        ExecutorService playerExecutor = ThreadPool.getPlayerExecutor(playerId);
        playerExecutor.submit(() -> {
            Player player = null;
            boolean containsPlayer = Players.containsPlayer(playerId);
            if (containsPlayer) {// 顶号
                player = Players.getPlayer(playerId);
                Channel oldChannel = player.getChannel();
                oldChannel.attr(AttributeKeys.PLAYER).set(null);
                oldChannel.writeAndFlush(new MessageCode<>(ToClientProtocol.KICK_OUT));
                try {
                    oldChannel.close().sync();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                player.setChannel(channel);
            } else {
                player = new Player(playerId, channel, playerExecutor);
                Players.addPlayer(player);
            }
            channel.attr(AttributeKeys.PLAYER).set(player);
        }).get();
    }

    @Override
    public ClientProtocol getProtocol() {
        return ClientProtocol.TOKEN_VALIDATE;
    }
}
