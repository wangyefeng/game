package org.game.gate;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.game.common.Server;
import org.game.common.util.JsonUtil;
import org.game.gate.net.TcpServer;
import org.game.gate.net.WebsocketServer;
import org.game.gate.net.client.ClientGroup;
import org.game.gate.net.client.LogicClient;
import org.game.gate.thread.ThreadPool;
import org.game.gate.zookepper.ZookeeperProperties;
import org.game.proto.MsgHandlerFactory;
import org.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

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
    private WebsocketServer webSocketServer;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private MsgHandlerFactory msgHandlerFactory;

    @Autowired
    private SpringConfig springConfig;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(Level.PARANOID);
    }

    @Override
    protected void start0() {
        Protocols.init();
        startZkServiceListener();
    }

    /**
     * 启动zookeeper服务发现监听器
     */
    private void startZkServiceListener() {
        String servicePath = springConfig.getServicePath();
        // 创建缓存
        CuratorCache cache = CuratorCache.build(zkClient, servicePath);
        // 添加监听器
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forCreates(childData -> {
                    if (!servicePath.equals(childData.getPath())) {// 过滤掉非服务节点
                        // 新增节点
                        log.info("新增logic服务器节点：{}", childData.getPath());
                        ServerInfo serverInfo = JsonUtil.parseJson(new String(childData.getData()), ServerInfo.class);
                        LogicClient logicClient = new LogicClient(childData.getPath(), serverInfo.host, serverInfo.tcpPort, serverInfo.rpcPort, msgHandlerFactory);
                        logicClient.start();
                        clientGroup.add(logicClient);
                    }
                })
                .forDeletes(childData -> {
                    if (!servicePath.equals(childData.getPath())) {// 过滤掉非服务节点
                        // 删除节点
                        log.info("删除logic服务器节点：{}", childData.getPath());
                        LogicClient client = clientGroup.remove(childData.getPath());
                        try {
                            client.close();
                        } catch (InterruptedException e) {
                            log.error("关闭LogicClient异常", e);
                        }
                    }
                })
                .build();
        cache.listenable().addListener(listener);
        // 启动缓存监听
        cache.start();
    }

    private record ServerInfo(String host, int tcpPort, int rpcPort) {
    }

    @Override
    protected void afterStart() {
        // 启动tcp服务
        tcpServer.start();
        // 启动ws服务
        webSocketServer.start();
    }

    public void stop() throws Exception {
        tcpServer.close();
        webSocketServer.close();
        clientGroup.close();
        ThreadPool.close();
        SpringApplication.exit(applicationContext);
    }


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Gate.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }
}
