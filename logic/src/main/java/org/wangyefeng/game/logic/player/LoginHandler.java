package org.wangyefeng.game.logic.player;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.logic.data.Item;
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
        channel.writeAndFlush(new MessagePlayer<>(playerId, LogicToClientProtocol.LOGIN, message));
        playerRepository.findById(playerId).ifPresentOrElse(player -> {
            player.setName("tttt");
            playerRepository.save(player);
        }, () -> {
            Player player = new Player(playerId, "test");
            player.getBag().addItem(new Item(1, 10));
            playerRepository.insert(player);
        });
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LOGIN;
    }
}
