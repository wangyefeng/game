package org.game.proto;

import com.google.protobuf.Message;
import io.netty.channel.Channel;

public interface PlayerMsgHandler<T extends Message> extends MsgHandler<T> {

    void handle(Channel channel, int playerId, T data) throws Exception;
}
