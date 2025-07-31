package org.game.gate.handler.logic;

import com.google.protobuf.Int32Value;
import io.netty.channel.Channel;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.game.proto.AbstractCodeMsgHandler;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.protocol.LogicToGateProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KickOutHandler extends AbstractCodeMsgHandler<Int32Value> {

    private static final Logger log = LoggerFactory.getLogger(KickOutHandler.class);

    @Override
    public void handle(Channel channel, Int32Value msg) {
        int playerId = msg.getValue();
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
