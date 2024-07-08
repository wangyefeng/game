package org.wangyefeng.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;

public abstract class AbstractNoMessageHandler implements Handler<Message> {

    public AbstractNoMessageHandler() {
    }

    public void handle(Channel channel, Message message) {
        handle0(channel);
    }

    abstract public void handle0(Channel channel);
}
