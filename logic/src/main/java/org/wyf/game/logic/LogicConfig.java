package org.wyf.game.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;

@ConfigurationProperties(prefix = "logic")
public class LogicConfig {

    private final int serverId;

    private final int tcpPort;

    private final int rpcPort;

    private final String host;

    public LogicConfig(int serverId, int tcpPort, int rpcPort, @Value("${logic.host:null}") String host) {
        this.serverId = serverId;
        this.tcpPort = tcpPort;
        this.rpcPort = rpcPort;
        if (host != null) {
            this.host = host;
        } else {
            try {
                this.host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getServerId() {
        return serverId;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public String getHost() {
        return host;
    }
}
