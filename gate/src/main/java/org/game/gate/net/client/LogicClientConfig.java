package org.game.gate.net.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LogicClientConfig {

    private static final String SERVICE_REGISTRY_ZNODE = "/logic";
    private static final Logger log = LoggerFactory.getLogger(LogicClientConfig.class);

    @Autowired
    private ZooKeeper zooKeeper;

    @Bean(destroyMethod = "")
    public LogicClient logicClient() throws InterruptedException, KeeperException {
        String servicePath = SERVICE_REGISTRY_ZNODE;
        List<String> serviceNodes = zooKeeper.getChildren(servicePath, false);
        if (serviceNodes.isEmpty()) {
            log.info("等待逻辑服注册服务...");
            while (serviceNodes.isEmpty()) {
                Thread.sleep(1000);
                serviceNodes = zooKeeper.getChildren(servicePath, false);
            }
            log.info("逻辑服注册服务成功！ 地址：{}", serviceNodes.get(0));
        }
        for (String node : serviceNodes) {
            String[] address = node.split(":");
            LogicClient logicClient = new LogicClient(address[0], Integer.parseInt(address[1]));
            return logicClient;
        }
        return null;
    }
}
