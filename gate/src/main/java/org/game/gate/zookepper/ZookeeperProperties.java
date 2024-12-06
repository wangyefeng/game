package org.game.gate.zookepper;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {

    private final String address;

    private final int timeout;

    ZookeeperProperties(String address, int timeout) {
        this.address = address;
        this.timeout = timeout;
    }

    public String getAddress() {
        return address;
    }

    public int getTimeout() {
        return timeout;
    }
}
