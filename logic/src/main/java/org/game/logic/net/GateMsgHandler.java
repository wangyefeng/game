package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.proto.CodeMsgHandler;
import org.game.proto.protocol.GateToLogicProtocol;

public abstract class GateMsgHandler<T extends Message> implements CodeMsgHandler<T> {

    @Override
    public void handle(Channel channel, T data) throws Exception {
        handle0(channel, data, Configs.getInstance());
    }

    protected abstract void handle0(Channel channel, T data, Configs configs) throws Exception;

    public abstract GateToLogicProtocol getProtocol();
}
