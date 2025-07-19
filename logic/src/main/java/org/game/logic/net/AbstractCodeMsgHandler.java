package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.proto.CodeMsgHandler;
import org.game.proto.protocol.GateToLogicProtocol;

public abstract class AbstractCodeMsgHandler<T extends Message> implements CodeMsgHandler<T> {

    @Override
    public void handle(Channel channel, T data) throws Exception {
        handle0(channel, data);
    }

    protected abstract void handle0(Channel channel, T data) throws Exception;

    public abstract GateToLogicProtocol getProtocol();
}
