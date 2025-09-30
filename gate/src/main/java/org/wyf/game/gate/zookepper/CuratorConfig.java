package org.wyf.game.gate.zookepper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class CuratorConfig {

    @Bean
    public CuratorFramework curatorFramework(ZookeeperProperties zookeeperProperties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperProperties.address(), zookeeperProperties.sessionTimeout(), zookeeperProperties.connectionTimeout(), retryPolicy);
        client.start();  // 启动客户端
        try {
            // 等待连接成功
            if (!client.blockUntilConnected(zookeeperProperties.connectionTimeout(), TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("连接 Zookeeper 超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("等待Zookeeper连接中断", e);
        }
        return client;
    }
}
