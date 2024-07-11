package org.wangyefeng.game.proto;

import com.google.protobuf.Parser;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractProtocolInMatcher implements ProtocolInMatcher {

    protected final Map<Short, Parser<?>> parsers = new HashMap<>();

    @Override
    public boolean match(short code) {
        return parsers.containsKey(code);
    }

    @Override
    public Parser<?> parser(short code) {
        return parsers.get(code);
    }

    protected void addParser(InProtocol protocol) {
        if (parsers.containsKey(protocol.getCode())) {
            throw new RuntimeException("Duplicate protocol code: " + protocol.getCode());
        }
        parsers.put(protocol.getCode(), protocol.parser());
    }
}
