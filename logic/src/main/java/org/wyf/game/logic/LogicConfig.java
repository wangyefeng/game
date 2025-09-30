package org.wyf.game.logic;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logic")
public record LogicConfig(int serverId, String host, int tcpPort, int rpcPort) {
}
