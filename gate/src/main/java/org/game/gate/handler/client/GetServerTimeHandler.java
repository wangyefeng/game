package org.game.gate.handler.client;

import com.google.protobuf.Empty;
import io.netty.channel.Channel;
import org.game.proto.CodeMsgHandler;
import org.game.proto.MessageCode;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.struct.System.PbGetServerTimeResp;
import org.springframework.stereotype.Component;

@Component
public final class GetServerTimeHandler implements CodeMsgHandler<Empty> {

    @Override
    public void handle(Channel channel, Empty message) {
        channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.GET_SERVER_TIME, PbGetServerTimeResp.newBuilder().setCurrentTimeMillis(System.currentTimeMillis()).build()));
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.GET_SERVER_TIME;
    }
}
