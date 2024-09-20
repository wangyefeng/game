package org.game.gate.handler.client;

import io.netty.channel.Channel;
import org.game.gate.net.AttributeKeys;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.game.gate.thread.ThreadPool;
import org.game.proto.MessageCode;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.struct.Common;

import java.util.concurrent.ThreadPoolExecutor;

@Component
public final class ValidateHandler implements ClientMsgHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(ValidateHandler.class);

    @Override
    public void handle(Channel channel, Common.PbInt msg) throws Exception {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("Player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }
        int playerId = msg.getVal();
        ThreadPoolExecutor playerExecutor = ThreadPool.getPlayerExecutor(playerId);
        playerExecutor.submit(() -> {
            log.info("Player {} is logging in. channel: {}", playerId, channel.id());
            Player player = null;
            boolean containsPlayer = Players.containsPlayer(playerId);
            if (containsPlayer) {// 顶号
                player = Players.getPlayer(playerId);
                Channel oldChannel = player.getChannel();
                oldChannel.writeAndFlush(new MessageCode<>(GateToClientProtocol.KICK_OUT));
                oldChannel.attr(AttributeKeys.PLAYER).set(null);
                oldChannel.close();
                player.setChannel(channel);
            } else {
                player = new Player(playerId, channel, playerExecutor);
                Players.addPlayer(player);
            }
            channel.attr(AttributeKeys.PLAYER).set(player);
        }).get();
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.VALIDATE;
    }
}
