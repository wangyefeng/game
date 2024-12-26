package org.game.gate.zookepper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class CuratorConfig {

    @Bean
    public CuratorFramework curatorFramework(ZookeeperProperties zookeeperProperties) {
        // 使用 ExponentialBackoffRetry 来进行重试
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperProperties.getAddress(), zookeeperProperties.getSessionTimeout(), zookeeperProperties.getConnectionTimeout(), retryPolicy);
        client.start();  // 启动客户端
        return client;
    }
}
