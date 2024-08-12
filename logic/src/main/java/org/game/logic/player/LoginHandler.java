package org.game.logic.player;

import io.netty.channel.Channel;
import org.game.logic.data.PlayerInfo;
import org.game.logic.handler.ClientMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.game.logic.data.PlayerRepository;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Common;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class LoginHandler implements ClientMsgHandler<Common.PbInt> {


    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void handle(Channel channel, int playerId, Common.PbInt message) {
        log.info("LoginHandler: playerId: {}, message: {}", playerId, message);
        Player player = Players.getPlayer(playerId);
        if (player == null) {
            Optional<PlayerInfo> optional = playerRepository.findById(playerId);
            PlayerInfo playerInfo;
            if (optional.isPresent()) {
                playerInfo = optional.get();
            } else {
                playerInfo = new PlayerInfo(playerId, "test", new ArrayList<>());
                playerRepository.insert(playerInfo);
            }
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
