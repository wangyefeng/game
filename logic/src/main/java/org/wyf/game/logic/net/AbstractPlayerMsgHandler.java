package org.wyf.game.logic.net;

import com.google.protobuf.Message;
import org.wyf.game.proto.AbstractMsgHandler;
import org.wyf.game.proto.PlayerMsgHandler;

public abstract class AbstractPlayerMsgHandler<T extends Message> extends AbstractMsgHandler<T> implements PlayerMsgHandler<T> {
}