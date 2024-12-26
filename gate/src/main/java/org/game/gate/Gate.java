package org.game.gate;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.Watcher;
import org.game.common.Server;
import org.game.gate.handler.client.ClientMsgHandler;
import org.game.gate.handler.logic.LogicMsgHandler;
import org.game.gate.net.TcpServer;
import org.game.gate.net.client.ClientGroup;
import org.game.gate.net.client.LogicClient;
import org.game.gate.thread.ThreadPool;
import org.game.gate.zookepper.ZookeeperProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;

/**
 * @author wangyefeng
 * @description 网关服务器
 */
@SpringBootApplication
@EnableConfigurationProperties(ZookeeperProperties.class)
public class Gate extends Server {

    private static final Logger log = LoggerFactory.getLogger(Gate.class);

    private static final String SERVICE_REGISTRY_ZNODE = "/logic";

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private Collection<LogicMsgHandler<?>> logicMsgHandlers;

    @Autowired
    private Collection<ClientMsgHandler<?>> clientMsgHandlers;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientGroup clientGroup;

    @Autowired
    private CuratorFramework zkClient;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(Level.PARANOID);
    }

    @Override
    protected void start0() {
        registerHandler();
        connectLogic();
    }

    private void connectLogic() {
        try {
            // 连接逻辑服
            String servicePath = SERVICE_REGISTRY_ZNODE;
            try {
                List<String> serverInfos = zkClient.getChildren().forPath(servicePath);
                if (serverInfos.isEmpty()) {
                    log.warn("没有发现逻辑服节点，等待中！");
                    while (serverInfos.isEmpty()) {
                        Thread.sleep(1000);
                        serverInfos = zkClient.getChildren().forPath(servicePath);
                    }
                    log.info("发现逻辑服节点：{}个 继续启动....", serverInfos.size());
                }
                for (String serverId : serverInfos) {
                    String path = SERVICE_REGISTRY_ZNODE + "/" + serverId;
                    byte[] data = zkClient.getData().forPath(path);
                    String serverInfo = new String(data);
                    String[] logicAddress = serverInfo.split(":");
                    LogicClient logicClient = new LogicClient(serverId, logicAddress[0], Integer.parseInt(logicAddress[1]));
                    logicClient.start();
                    clientGroup.add(logicClient);
                }
                Watcher w = event -> {
                    log.info("节点{}数据发生变化，进行相关处理....", event.getPath());
                    try {
                        List<String> serviceNodes = zkClient.getChildren().forPath(servicePath);
                        for (String serverId : serviceNodes) {
                            if (clientGroup.contains(serverId)) {
                                continue;
                            }
                            String path = SERVICE_REGISTRY_ZNODE + "/" + serverId;
                            byte[] data = zkClient.getData().forPath(path);
                            String serverInfo = new String(data);
                            String[] logicAddress = serverInfo.split(":");
                            LogicClient logicClient = new LogicClient(serverId, logicAddress[0], Integer.parseInt(logicAddress[1]));
                            logicClient.start();
                            clientGroup.add(logicClient);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };

                zkClient.getChildren().usingWatcher(w).forPath(servicePath);
                log.info("连接ZooKeeper成功！！");
            } catch (Exception e) {
                throw new IllegalStateException("初始化ZooKeeper连接异常....", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("连接逻辑服失败！", e);
        }
    }

    @Override
    protected void afterStart() {
        // 最后启动tcp服务
        tcpServer.start();
    }

    public void stop() throws Exception {
        tcpServer.close();
        clientGroup.close();
        ThreadPool.shutdown();
        SpringApplication.exit(applicationContext);
    }

    private void registerHandler() {
        log.info("handler registering...");
        logicMsgHandlers.forEach(LogicMsgHandler::register);
        clientMsgHandlers.forEach(ClientMsgHandler::register);
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Gate.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }
}
