package org.wyf.game.logic.gate;

import com.google.protobuf.Empty;
import io.netty.channel.Channel;
import org.wyf.game.proto.AbstractCodeMsgHandler;
import org.wyf.game.proto.protocol.GateToLogicProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PingHandlerCode extends AbstractCodeMsgHandler<Empty> {

    private static final Logger log = LoggerFactory.getLogger(PingHandlerCode.class);

    public void handle(Channel channel, Empty data) {
        log.debug("Received a ping message from gate.");
    }

    @Override
    public GateToLogicProtocol getProtocol() {
        return GateToLogicProtocol.PING;
    }
}
