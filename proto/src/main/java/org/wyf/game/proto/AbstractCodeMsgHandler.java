package org.wyf.game.proto;

import com.google.protobuf.Message;

public abstract class AbstractCodeMsgHandler<T extends Message> extends AbstractMsgHandler<T> implements CodeMsgHandler<T> {
}
