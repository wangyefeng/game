package org.wangyefeng.game.proto;

import com.google.protobuf.Parser;

public interface ProtocolInMatcher {

    boolean match(short code);

    Parser<?> parser(short code);
}