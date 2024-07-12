package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.proto.MessagePlayer;
import org.wangyefeng.game.proto.protocol.Protocol;

public class ClientMsg<T extends Message> extends MessagePlayer<T> {

    public ClientMsg(int playerId, Protocol protocol, T message) {
        super(playerId, protocol, message);
    }

    public ClientMsg(int playerId, Protocol protocol) {
        super(playerId, protocol);
    }
}
