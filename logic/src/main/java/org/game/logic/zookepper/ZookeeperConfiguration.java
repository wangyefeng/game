package org.game.logic.zookepper;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperConfiguration.class);

    @Bean
    public ZooKeeper zooKeeper(ZookeeperProperties zookeeperProperties) {
        log.info("初始化ZooKeeper连接....");
        String address = zookeeperProperties.getAddress();
        int timeout = zookeeperProperties.getTimeout();
        ZooKeeper zooKeeper;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            zooKeeper = new ZooKeeper(address, timeout, event -> {
                if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
                    //如果收到了服务端的响应事件,连接成功
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            log.info("连接ZooKeeper成功！！");
        } catch (Exception e) {
            throw new IllegalStateException("初始化ZooKeeper连接异常....", e);
        }
        return zooKeeper;
    }
}