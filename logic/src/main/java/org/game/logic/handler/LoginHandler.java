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
public class LoginHandler implements ClientMsgHandler<Login.PbLogin> {


    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void handle(Channel channel, int playerId, Login.PbLogin message, Config config) {
        log.info("LoginHandler: playerId: {}, message: {}", playerId, message);
        Player player = Players.getPlayer(playerId);
        if (player == null) {
            player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel);
            PlayerService playerService = player.getService(PlayerService.class);
            if (playerService.playerExists()) {
                player.login();
            } else {
                log.warn("玩家登录失败，玩家不存在: playerId: {}", playerId);
                return;
            }
            Players.addPlayer(player);
        } else {
            player.setChannel(channel);
        }
        player.sendToClient(LogicToClientProtocol.LOGIN, message);
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LOGIN;
    }
}
