package org.wyf.game.gate.handler.client;

import com.google.protobuf.Empty;
import io.netty.channel.Channel;
import org.wyf.game.proto.AbstractCodeMsgHandler;
import org.wyf.game.proto.MessageCode;
import org.wyf.game.proto.protocol.ClientToGateProtocol;
import org.wyf.game.proto.protocol.GateToClientProtocol;
import org.wyf.game.proto.struct.System.PbGetServerTimeResp;
import org.springframework.stereotype.Component;

@Component
public final class GetServerTimeHandler extends AbstractCodeMsgHandler<Empty> {

    @Override
    public void handle(Channel channel, Empty message) {
        channel.writeAndFlush(MessageCode.of(GateToClientProtocol.GET_SERVER_TIME, PbGetServerTimeResp.newBuilder().setCurrentTimeMillis(System.currentTimeMillis()).build()));
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.GET_SERVER_TIME;
    }
}
