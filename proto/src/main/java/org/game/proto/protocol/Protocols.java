package org.game.proto.protocol;

import java.util.HashMap;
import java.util.Map;

public abstract class Protocols {

    private static final Map<Byte, Map<Byte, Map<Short, Protocol>>> protocolMap = new HashMap<>();

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
            protocolMap.get(protocol.from().getCode()).putIfAbsent(protocol.to().getCode(), new HashMap<>());
            protocolMap.get(protocol.from().getCode()).get(protocol.to().getCode()).put(protocol.getCode(), protocol);
        }
    }

    public static Protocol getProtocol(byte from, byte to, short code) {
        if (protocolMap.containsKey(from)) {
            Map<Byte, Map<Short, Protocol>> shortProtocolMap = protocolMap.get(from);
            if (shortProtocolMap.containsKey(to)) {
                return shortProtocolMap.get(to).get(code);
            }
            return null;
        }
        return null;
    }
}
