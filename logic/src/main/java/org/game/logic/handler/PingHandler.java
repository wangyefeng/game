package org.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.game.config.Config;
import org.game.proto.DecoderType;
import org.game.proto.Topic;
import org.game.proto.protocol.GateToLogicProtocol;
import org.game.proto.protocol.LogicToGateProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PingHandler implements GateMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private static final ByteBuf PONG = Unpooled.unreleasableBuffer(Unpooled.directBuffer(8));

    static {
        PONG.writeInt(4);
        PONG.writeByte(DecoderType.MESSAGE_CODE.getCode());
        PONG.writeByte(Topic.GATE.getCode());
        PONG.writeShort(LogicToGateProtocol.PONG.getCode());
    }

    @Override
    public void handle(Channel channel, Message msg, Config config) {
        log.debug("Received a ping message from gate.");
        channel.writeAndFlush(PONG.duplicate());// 回应PONG消息
    }

    @Override
    public GateToLogicProtocol getProtocol() {
        return GateToLogicProtocol.PING;
    }
}
