package org.wangyefeng.game.logic.player;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.logic.data.Player;
import org.wangyefeng.game.logic.data.PlayerRepository;
import org.wangyefeng.game.logic.handler.ClientMsgHandler;
import org.wangyefeng.game.proto.MessagePlayer;
import org.wangyefeng.game.proto.protocol.ClientToLogicProtocol;
import org.wangyefeng.game.proto.protocol.LogicToClientProtocol;
import org.wangyefeng.game.proto.struct.Common;

@Component
public class LoginHandler implements ClientMsgHandler<Common.PbInt> {


    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void handle(Channel channel, int playerId, Common.PbInt message) {
        log.info("LoginHandler: playerId: {}, message: {}", playerId, message);
        Player player = playerRepository.findById(playerId).get();
        if (player == null) {
            player = new Player(playerId, "default name");
            playerRepository.insert(player);
        }
        Players.addPlayer(player);
        channel.writeAndFlush(new MessagePlayer<>(playerId, LogicToClientProtocol.LOGIN, message));
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LOGIN;
    }
}
