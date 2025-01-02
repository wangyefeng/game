package org.game.gate.handler.client;

import io.netty.channel.Channel;
import org.game.common.util.TokenUtil;
import org.game.gate.net.AttributeKeys;
import org.game.gate.net.client.ClientGroup;
import org.game.gate.net.client.LogicClient;
import org.game.gate.net.client.LogicHandler;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.game.gate.thread.ThreadPool;
import org.game.proto.MessageCode;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.struct.Common;
import org.game.proto.struct.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
public final class ValidateHandler implements ClientMsgHandler<Login.PbValidate> {

    private static final Logger log = LoggerFactory.getLogger(ValidateHandler.class);

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Override
    public void handle(Channel channel, Login.PbValidate msg) throws Exception {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }

        int playerId = msg.getId();
        String token = msg.getToken();
        if (!TokenUtil.verify(token, playerId)) {
            log.warn("player {} token verify failed.", playerId);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.TOKEN_VALIDATE, Common.PbBool.newBuilder().setVal(false).build()));
            return;
        }
        ThreadPoolExecutor playerExecutor = ThreadPool.getPlayerExecutor(playerId);
        playerExecutor.submit(() -> {
            log.info("Player {} is logging in. channel: {}", playerId, channel.id());
            Player player;
            boolean containsPlayer = Players.containsPlayer(playerId);
            if (containsPlayer) {// 顶号
                player = Players.getPlayer(playerId);
                Channel oldChannel = player.getChannel();
                oldChannel.writeAndFlush(new MessageCode<>(GateToClientProtocol.KICK_OUT));
                oldChannel.attr(AttributeKeys.PLAYER).set(null);
                oldChannel.close();
                player.setChannel(channel);
            } else {
                if (clientGroup.getClients().isEmpty()) {
                    channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.KICK_OUT));
                    channel.attr(AttributeKeys.PLAYER).set(null);
                    channel.close();
                    return;
                }
                player = new Player(playerId, channel, playerExecutor, clientGroup.next());
                Players.addPlayer(player);
            }
            channel.attr(AttributeKeys.PLAYER).set(player);
            player.getLogicClient().getChannel().attr(LogicHandler.PLAYERS_KEY).get().add(player.getId());
        }).get();
        channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.TOKEN_VALIDATE, Common.PbBool.newBuilder().setVal(true).build()));
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.VALIDATE;
    }
}
