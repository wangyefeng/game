package org.wyf.game.proto;

import com.google.protobuf.Message;
import io.netty.channel.Channel;

public interface CodeMsgHandler<M extends Message> extends MsgHandler<M> {

    void handle(Channel channel, M msg) throws Exception;
}
