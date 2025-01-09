package org.game.proto.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 协议管理类
 *
 * @author 王叶峰
 */
public abstract class Protocols {

    private static final Map<Key, Protocol> protocolMap = new HashMap<>();

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
            Protocol p = protocolMap.put(new Key(from, to, code), protocol);
            if (p != null) {
                throw new IllegalArgumentException(Protocol.toString(protocol) + "与" + Protocol.toString(p) + "协议号冲突！！！");
            }
        }
    }

    public static Protocol getProtocol(byte from, byte to, short code) {
        return protocolMap.get(new Key(from, to, code));
    }

    private record Key(byte from, byte to, short code) {}
}
