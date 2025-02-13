package org.game.gate;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.game.common.Server;
import org.game.common.util.JsonUtil;
import org.game.gate.net.TcpServer;
import org.game.gate.net.client.ClientGroup;
import org.game.gate.net.client.LogicClient;
import org.game.gate.thread.ThreadPool;
import org.game.gate.zookepper.ZookeeperProperties;
import org.game.proto.MsgHandler;
import org.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wangyefeng
 * @description 网关服务器
 */
@SpringBootApplication
@EnableConfigurationProperties(ZookeeperProperties.class)
public class Gate extends Server {

    private static final Logger log = LoggerFactory.getLogger(Gate.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Autowired
    private CuratorFramework zkClient;

    @Value("${zookeeper.root-path}")
    private String servicePath;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(Level.PARANOID);
    }

    @Override
    protected void start0() {
        ThreadPool.start();
        Protocols.init();
        registerHandler();
        connectLogic();
    }

    private void connectLogic() {
        try {
            // 连接逻辑服
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
                    String path = servicePath + "/" + serverId;
                    byte[] data = zkClient.getData().forPath(path);
                    ServerInfo serverInfo = JsonUtil.parseJson(new String(data), ServerInfo.class);
                    LogicClient logicClient = new LogicClient(Integer.parseInt(serverId), serverInfo.host, serverInfo.tcpPort, serverInfo.rpcPort);
                    logicClient.start();
                    clientGroup.add(logicClient);
                }
                Watcher w = new LogicWatcher(zkClient, clientGroup, servicePath);
                zkClient.getChildren().usingWatcher(w).forPath(servicePath);
                log.info("连接ZooKeeper成功！！");
            } catch (Exception e) {
                throw new IllegalStateException("初始化ZooKeeper连接异常....", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("连接逻辑服失败！", e);
        }
    }

    private record ServerInfo(String host, int tcpPort, int rpcPort) {}

    private record LogicWatcher(CuratorFramework zkClient, ClientGroup<LogicClient> clientGroup, String servicePath) implements Watcher {

        @Override
            public void process(WatchedEvent event) {
                try {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        log.info("逻辑服务器节点发生变化，进行相关处理....");
                        List<String> serviceNodes = zkClient.getChildren().forPath(servicePath);
                        Set<Integer> nodes = new HashSet<>(serviceNodes.size());
                        for (String serverId : serviceNodes) {
                            int id = Integer.parseInt(serverId);
                            nodes.add(id);
                            if (clientGroup.contains(id)) {
                                continue;
                            }
                            String path = servicePath + "/" + serverId;
                            byte[] data = zkClient.getData().forPath(path);
                            ServerInfo serverInfo = JsonUtil.parseJson(new String(data), ServerInfo.class);
                            LogicClient logicClient = new LogicClient(Integer.parseInt(serverId), serverInfo.host, serverInfo.tcpPort, serverInfo.rpcPort);
                            logicClient.start();
                            clientGroup.add(logicClient);
                        }
                        clientGroup.getClients().values().removeIf(client -> {
                            if (!nodes.contains(client.getId())) {
                                try {
                                    client.close();
                                } catch (Exception e) {
                                    log.error("关闭逻辑服务器{}异常", client.getId(), e);
                                }
                                return true;
                            }
                            return false;
                        });
                    }
                    if (event.getState() != KeeperState.Closed) {
                        // 重新注册 Watcher 以继续监听节点变化
                        zkClient.getChildren().usingWatcher(this).forPath(servicePath);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
        applicationContext.getBeansOfType(MsgHandler.class).values().forEach(MsgHandler::register);// 注册所有handler
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Gate.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }
}
