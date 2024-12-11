package org.game.logic.player;

import io.netty.channel.Channel;
import org.game.config.Config;
import org.game.logic.data.entity.PlayerInfo;
import org.game.logic.data.repository.PlayerDao;
import org.game.logic.handler.ClientMsgHandler;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class LoginHandler implements ClientMsgHandler<Common.PbInt> {


    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Autowired
    private PlayerDao playerDao;

    @Override
    public void handle(Channel channel, int playerId, Common.PbInt message, Config config) {
        log.info("LoginHandler: playerId: {}, message: {}", playerId, message);
        Player player = Players.getPlayer(playerId);
        if (player == null) {
            PlayerInfo playerInfo = playerDao.findById(playerId).orElseGet(() -> playerDao.insert(new PlayerInfo(playerId, "test", new ArrayList<>())));
            player = new Player(playerInfo, channel);
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
