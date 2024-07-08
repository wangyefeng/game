package org.wangyefeng.game.gate.handler;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.gate.net.GateMessage;
import org.wangyefeng.game.gate.protocol.C2SProtocol;
import org.wangyefeng.game.gate.protocol.S2CProtocol;

@Component
public class PingHandler extends AbstractNoMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private static final GateMessage PONG = new GateMessage<>(S2CProtocol.PONG);

    @Override
    public void handle0(Channel channel) {
        log.info("Received a ping message from client.");
        channel.writeAndFlush(PONG);// 回应PONG消息
    }

    @Override
    public C2SProtocol getProtocol() {
        return C2SProtocol.PING;
    }
}
