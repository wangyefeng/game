package org.game.gate.handler.logic;

import io.netty.channel.Channel;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.protocol.LogicToGateProtocol;
import org.game.proto.struct.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KickOutHandler implements LogicMsgHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(KickOutHandler.class);

    @Override
    public void handle(Channel channel, Common.PbInt msg) throws Exception {
        int playerId = msg.getVal();
        Player player = Players.getPlayer(playerId);
        if (player != null) {
            player.writeToClient(GateToClientProtocol.KICK_OUT);
            player.getChannel().close();
        }
    }

    @Override
    public LogicToGateProtocol getProtocol() {
        return LogicToGateProtocol.KICK_OUT;
    }
}
