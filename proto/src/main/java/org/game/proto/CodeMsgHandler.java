package org.game.proto;

import com.google.protobuf.Message;
import io.netty.channel.Channel;

public interface CodeMsgHandler<T extends Message> extends MsgHandler<T> {

    void handle(Channel channel, T msg) throws Exception;
}
