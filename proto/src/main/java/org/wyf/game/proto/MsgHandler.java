package org.wyf.game.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.wyf.game.proto.protocol.Protocol;

import java.io.InputStream;

public interface MsgHandler<M extends Message> {

    Protocol getProtocol();

    M parseFrom(InputStream input) throws InvalidProtocolBufferException;

}
