package org.game.gate;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
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
        addZkListener();
        initLogicClient();
    }

    private void addZkListener() {
        zkClient.getConnectionStateListenable().addListener((_, state) -> {
            switch (state) {
                case RECONNECTED:
                    log.info("zookeeper 断线重连成功，开始恢复业务...");
                    try {
                        registerZkService();
                    } catch (Exception e) {
                        log.error("zookeeper 注册服务失败", e);
                    }
                    break;
                case SUSPENDED:
                    log.info("zookeeper 连接断开，等待重连...");
                    break;
                default:
                    break;
            }
        });
    }

    private void registerZkService() {
        // 连接逻辑服
        try {
            List<String> serverInfos = zkClient.getChildren().forPath(servicePath);
            Set<String> serverIdSet = new HashSet<>(serverInfos);
            for (String serverId : serverInfos) {
                String id = servicePath + "/" + serverId;
                serverIdSet.add(id);
                if (clientGroup.contains(id)) {
                    continue;
                }
                byte[] data = zkClient.getData().forPath(id);
                ServerInfo serverInfo = JsonUtil.parseJson(new String(data), ServerInfo.class);
                LogicClient logicClient = new LogicClient(id, serverInfo.host, serverInfo.tcpPort, serverInfo.rpcPort);
                logicClient.start();
                clientGroup.add(logicClient);
            }
            clientGroup.getClients().values().removeIf(logicClient -> {
                if (serverIdSet.contains(logicClient.getId())) {
                    try {
                        logicClient.close();
                    } catch (InterruptedException e) {
                        log.error("关闭logic客户端异常", e);
                    }
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            throw new IllegalStateException("重建ZooKeeper连接异常....", e);
        }
    }

    /**
     * 初始化LogicClient连接
     */
    private void initLogicClient() {
        // 创建缓存
        CuratorCache cache = CuratorCache.build(zkClient, servicePath);
        // 添加监听器
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forPathChildrenCache(servicePath, zkClient, (_, event) -> {
                    PathChildrenCacheEvent.Type type = event.getType();
                    if (type == PathChildrenCacheEvent.Type.CHILD_ADDED) {
                        // 新增节点
                        ChildData childData = event.getData();
                        log.info("新增logic服务器节点：{}", childData.getPath());
                        ServerInfo serverInfo = JsonUtil.parseJson(new String(childData.getData()), ServerInfo.class);
                        LogicClient logicClient = new LogicClient(childData.getPath(), serverInfo.host, serverInfo.tcpPort, serverInfo.rpcPort);
                        logicClient.start();
                        clientGroup.add(logicClient);
                    } else if (type == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                        // 删除节点
                        log.info("删除logic服务器节点：{}", event.getData().getPath());
                        LogicClient client = clientGroup.remove(event.getData().getPath());
                        client.close();
                    }

                })
                .build();
        cache.listenable().addListener(listener);
        cache.start(); // 启动缓存监听
    }

    private record ServerInfo(String host, int tcpPort, int rpcPort) {
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
