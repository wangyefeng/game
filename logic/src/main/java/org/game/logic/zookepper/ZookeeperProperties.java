package org.game.logic.zookepper;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public record ZookeeperProperties(String address, int connectionTimeout, int sessionTimeout, String rootPath) {
}
