package org.wangyefeng.game.logic.player;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.logic.data.PlayerInfo;
import org.wangyefeng.game.logic.data.PlayerRepository;
import org.wangyefeng.game.logic.handler.ClientMsgHandler;
import org.wangyefeng.game.proto.MessagePlayer;
import org.wangyefeng.game.proto.protocol.ClientToLogicProtocol;
import org.wangyefeng.game.proto.protocol.LogicToClientProtocol;
import org.wangyefeng.game.proto.struct.Common;

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
            PlayerInfo playerInfo = optional.orElse(new PlayerInfo(playerId, "test", new ArrayList<>()));
            if (!optional.isPresent()) {
                playerRepository.insert(playerInfo);
            }
            player = new Player(playerInfo, channel);
            Players.addPlayer(player);
        } else {
            player.setChannel(channel);
        }
        channel.writeAndFlush(new MessagePlayer<>(playerId, LogicToClientProtocol.LOGIN, message));
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LOGIN;
    }
}
