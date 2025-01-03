package org.game.proto.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 协议管理类
 *
 * @author 王叶峰
 */
public abstract class Protocols {

    private static final Map<Byte, Map<Byte, Map<Short, Protocol>>> protocolMap = new HashMap<>();

    public static void init() {
        LogicToGateProtocol.register();
        ClientToLogicProtocol.register();
        GateToClientProtocol.register();
        GateToLogicProtocol.register();
        ClientToGateProtocol.register();
        LogicToClientProtocol.register();
    }

    public static void addProtocols(Protocol[] protocols) {
        for (Protocol protocol : protocols) {
            byte from = protocol.from().getCode();
            byte to = protocol.to().getCode();
            short code = protocol.getCode();
            Protocol p = protocolMap.computeIfAbsent(from, _ -> new HashMap<>())
                    .computeIfAbsent(to, _ -> new HashMap<>())
                    .put(code, protocol);
            if (p != null) {
                throw new IllegalArgumentException("协议号[" + code + "]冲突。" + protocol.getClass().getSimpleName() + "." + protocol + "与" + protocol.getClass().getSimpleName() + "." + p);
            }
        }
    }

    public static Protocol getProtocol(byte from, byte to, short code) {
        Map<Byte, Map<Short, Protocol>> m = protocolMap.get(from);
        if (m != null) {
            Map<Short, Protocol> k = m.get(to);
            if (k != null) {
                return k.get(code);
            }
        }
        return null;
    }
}
