package org.wangyefeng.game.proto.protocol;

import java.util.HashMap;
import java.util.Map;

public abstract class ProtocolUtils {

    private static final Map<Byte, Map<Short, Protocol>> protocolMap = new HashMap<>();

    static {
        addProtocols(ClientToGateProtocol.values());
        addProtocols(ClientToLogicProtocol.values());
        addProtocols(GateToClientProtocol.values());
        addProtocols(GateToLogicProtocol.values());
        addProtocols(LogicToClientProtocol.values());
        addProtocols(LogicToGateProtocol.values());
    }

    private static void addProtocols(Protocol[] protocols) {
        for (Protocol protocol : protocols) {
            protocolMap.putIfAbsent(protocol.from().getCode(), new HashMap<>());
            protocolMap.get(protocol.from().getCode()).put(protocol.getCode(), protocol);
        }
    }

    public static Protocol getProtocol(byte from, short code) {
        if (protocolMap.containsKey(from)) {
            Map<Short, Protocol> shortProtocolMap = protocolMap.get(from);
            return shortProtocolMap.get(code);
        }
        return null;
    }
}
