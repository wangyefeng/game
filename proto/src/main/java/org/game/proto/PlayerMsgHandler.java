package org.game.proto;

import com.google.protobuf.Message;
import io.netty.channel.Channel;

public interface PlayerMsgHandler<M extends Message> extends MsgHandler<M> {

    void handle(Channel channel, int playerId, M data) throws Exception;
}
