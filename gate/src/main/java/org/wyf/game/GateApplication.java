package org.wyf.game;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.wyf.game.common.Server;
import org.wyf.game.common.util.JsonUtil;
import org.wyf.game.gate.net.TcpServer;
import org.wyf.game.gate.net.client.ClientGroup;
import org.wyf.game.gate.net.client.LogicClient;
import org.wyf.game.gate.thread.ThreadPool;
import org.wyf.game.gate.zookepper.ZookeeperProperties;
import org.wyf.game.proto.MsgHandlerFactory;
import org.wyf.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * 网关服务器
 */
@SpringBootApplication
public class GateApplication extends Server {

    private static final Logger log = LoggerFactory.getLogger(GateApplication.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private MsgHandlerFactory msgHandlerFactory;

    @Autowired
    private ZookeeperProperties zookeeperProperties;

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
        String rootPath = zookeeperProperties.rootPath();
        // 创建缓存
        CuratorCache cache = CuratorCache.build(zkClient, rootPath);
        // 添加监听器
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forCreates(childData -> {
                    if (!rootPath.equals(childData.getPath())) {// 过滤掉非服务节点
                        // 新增节点
                        log.info("新增logic服务器节点：{}", childData.getPath());
                        ServerInfo serverInfo = JsonUtil.parseJson(new String(childData.getData()), ServerInfo.class);
                        LogicClient logicClient = new LogicClient(childData.getPath(), serverInfo.host, serverInfo.tcpPort, serverInfo.rpcPort, msgHandlerFactory);
                        logicClient.start();
                        clientGroup.add(logicClient);
                    }
                })
                .forDeletes(childData -> {
                    if (!rootPath.equals(childData.getPath())) {// 过滤掉非服务节点
                        // 删除节点
                        log.info("删除logic服务器节点：{}", childData.getPath());
                        LogicClient client = clientGroup.remove(childData.getPath());
                        client.close();
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
    }

    public void stop() throws Exception {
        tcpServer.close();
        clientGroup.close();
        ThreadPool.close();
        SpringApplication.exit(applicationContext);
    }


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GateApplication.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }
}
