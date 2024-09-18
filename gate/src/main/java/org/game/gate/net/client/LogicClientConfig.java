package org.game.gate.net.client;


import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LogicClientConfig {

    private static final String SERVICE_REGISTRY_ZNODE = "/logic";

    @Autowired
    private ZooKeeper zooKeeper;

    @Bean(destroyMethod = "")
    public LogicClient logicClient() throws InterruptedException, KeeperException {
        String servicePath = SERVICE_REGISTRY_ZNODE;
        List<String> serviceNodes = zooKeeper.getChildren(servicePath, false);
        for (String node : serviceNodes) {
            String[] address = node.split(":");
            LogicClient logicClient = new LogicClient(address[0], Integer.parseInt(address[1]));
            return logicClient;
        }
        throw new RuntimeException("No available logic server found");
    }
}
