package org.wyf.game.gate.handler.logic;

import com.google.protobuf.Empty;
import io.netty.channel.Channel;
import org.wyf.game.proto.AbstractCodeMsgHandler;
import org.wyf.game.proto.protocol.LogicToGateProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogicPingHandler extends AbstractCodeMsgHandler<Empty> {

    private static final Logger log = LoggerFactory.getLogger(LogicPingHandler.class);

    @Override
    public void handle(Channel channel, Empty message) {
        log.debug("Received a ping message from logic.");
    }


    @Override
    public LogicToGateProtocol getProtocol() {
        return LogicToGateProtocol.PING;
    }
}
