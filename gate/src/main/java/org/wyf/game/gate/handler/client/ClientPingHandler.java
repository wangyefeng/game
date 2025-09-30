package org.wyf.game.gate.handler.client;

import com.google.protobuf.Empty;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.wyf.game.proto.AbstractCodeMsgHandler;
import org.wyf.game.proto.DecoderType;
import org.wyf.game.proto.Topic;
import org.wyf.game.proto.protocol.ClientToGateProtocol;
import org.wyf.game.proto.protocol.GateToClientProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public final class ClientPingHandler extends AbstractCodeMsgHandler<Empty> {

    private static final Logger log = LoggerFactory.getLogger(ClientPingHandler.class);

    private static final ByteBuf PONG = Unpooled.unreleasableBuffer(Unpooled.directBuffer(8, 8));


    static {
        PONG.writeInt(4);
        PONG.writeByte(DecoderType.MESSAGE_CODE.getCode());
        PONG.writeByte(Topic.GATE.getCode());
        PONG.writeShort(GateToClientProtocol.PONG.getCode());
    }

    @Override
    public void handle(Channel channel, Empty message) {
        log.debug("Received a ping message from client.");
        channel.writeAndFlush(PONG.duplicate());
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.PING;
    }
}
