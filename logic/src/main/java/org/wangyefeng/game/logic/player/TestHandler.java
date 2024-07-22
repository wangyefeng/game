package org.wangyefeng.game.logic.player;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.proto.MessagePlayer;
import org.wangyefeng.game.proto.protocol.ClientToLogicProtocol;
import org.wangyefeng.game.proto.protocol.LogicToClientProtocol;
import org.wangyefeng.game.proto.struct.Common;

@Component
public class TestHandler extends PlayerHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(TestHandler.class);

    @Override
    protected void handle(Player player, Common.PbInt message, Channel channel) {
        log.info("TestHandler {} received message: {}", player.getPlayerInfo().getName(), message);
        channel.writeAndFlush(new MessagePlayer<>(player.getId(), LogicToClientProtocol.TEST, message));
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.TEST;
    }
}
