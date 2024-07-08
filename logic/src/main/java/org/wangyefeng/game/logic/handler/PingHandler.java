package org.wangyefeng.game.logic.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.logic.net.ProtocolType;
import org.wangyefeng.game.logic.protocol.GateProtocol;
import org.wangyefeng.game.logic.protocol.ToGateProtocol;

@Component
public class PingHandler extends AbstractNoMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private static final ByteBuf PONG = Unpooled.unreleasableBuffer(Unpooled.directBuffer(7));

    static {
        PONG.writeInt(3);
        PONG.writeByte(ProtocolType.LOGIC_GATE.getValue());
        PONG.writeShort(ToGateProtocol.PONG.getCode());
    }

    @Override
    public void handle0(Channel channel) {
        log.info("Received a ping message from gate.");
        channel.writeAndFlush(PONG.duplicate());// 回应PONG消息
    }

    @Override
    public GateProtocol getProtocol() {
        return GateProtocol.PING;
    }
}
