package org.game.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.game.proto.protocol.Protocol;

import java.io.InputStream;

public interface MsgHandler<T extends Message> {

    Protocol getProtocol();

    T parseFrom(InputStream input) throws InvalidProtocolBufferException;

}
