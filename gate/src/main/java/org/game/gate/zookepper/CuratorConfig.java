package org.game.gate.zookepper;

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
        // 使用 ExponentialBackoffRetry 来进行重试
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperProperties.getAddress(), zookeeperProperties.getSessionTimeout(), zookeeperProperties.getConnectionTimeout(), retryPolicy);
        client.start();  // 启动客户端
        try {
            // 等待最多 5 秒连接成功（阻塞）
            if (!client.blockUntilConnected(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("连接 Zookeeper 超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("等待Zookeeper连接中断", e);
        }
        return client;
    }
}
