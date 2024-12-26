package org.game.gate.zookepper;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {

    private final String address;

    private final int connectionTimeout;

    private final int sessionTimeout;

    public ZookeeperProperties(String address, int connectionTimeout, int sessionTimeout) {
        this.address = address;
        this.connectionTimeout = connectionTimeout;
        this.sessionTimeout = sessionTimeout;
    }

    public String getAddress() {
        return address;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }
}
