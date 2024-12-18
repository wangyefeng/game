package org.game.logic.handler;

import io.netty.channel.Channel;
import org.game.config.Config;
import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.game.logic.service.GameService;
import org.game.logic.service.PlayerService;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RegisterHandler implements ClientMsgHandler<Login.PbRegister> {


    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void handle(Channel channel, int playerId, Login.PbRegister message, Config config) {
        log.info("玩家{}注册 信息: {}", playerId, message);
        Player player = Players.getPlayer(playerId);
        if (player != null) {
            return;
        }
        player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel);
        PlayerService playerService = player.getService(PlayerService.class);
        if (playerService.playerExists()) {
            return;
        }
        player.register(message);
        Players.addPlayer(player);
        player.sendToClient(LogicToClientProtocol.REGISTER, message);
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.REGISTER;
    }
}
