package org.wyf.game;

import io.netty.util.ResourceLeakDetector;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.wyf.game.common.Server;
import org.wyf.game.common.util.JsonUtil;
import org.wyf.game.config.service.ConfigService;
import org.wyf.game.logic.ExitStatus;
import org.wyf.game.logic.LogicConfig;
import org.wyf.game.logic.net.TcpServer;
import org.wyf.game.logic.player.Player;
import org.wyf.game.logic.player.Players;
import org.wyf.game.logic.player.function.TimeIntervalManager;
import org.wyf.game.logic.thread.ThreadPool;
import org.wyf.game.logic.zookepper.ZookeeperProperties;
import org.wyf.game.proto.protocol.Protocols;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class LogicApplication extends Server {

    private static final Logger log = LoggerFactory.getLogger(LogicApplication.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private io.grpc.Server grpcServer;

    @Autowired
    private TimeIntervalManager timeIntervalManager;

    @Autowired
    private LogicConfig logicConfig;

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    @Autowired
    private ConfigService configService;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    static void main(String[] args) {
        SpringApplication application = new SpringApplication(LogicApplication.class);
        application.setRegisterShutdownHook(false);// 关闭Spring-boot停服处理策略
        application.run(args);
    }

    /**
     * 初始化spring容器后，启动服务器
     */
    @Override
    protected void start0() throws Exception {
        configService.initConfig();
        Protocols.init();
        ThreadPool.start();
        timeIntervalManager.init();
    }


    @Override
    protected void afterStart() throws Exception {
        tcpServer.start();
        grpcServer.start();
        initZkService();
    }

    private void initZkService() throws Exception {
        addZkListener();
        registerZkService();
    }

    /**
     * 注册zookeeper服务
     *
     * @throws Exception 异常
     */
    private void registerZkService() throws Exception {
        String servicePath = zookeeperProperties.rootPath() + "/" + logicConfig.getServerId();
        String serverJson = JsonUtil.toJson(new ServerInfo(InetAddress.getLocalHost().getHostAddress(), tcpServer.getPort(), grpcServer.getPort()));
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(servicePath, serverJson.getBytes());
        log.info("zookeeper registry service success, path: {}", servicePath);
    }

    private void addZkListener() {
        zkClient.getConnectionStateListenable().addListener((_, state) -> {
            switch (state) {
                case SUSPENDED -> log.warn("zookeeper连接断开，正在重试...");
                case RECONNECTED -> {
                    String servicePath = zookeeperProperties.rootPath() + "/" + logicConfig.getServerId();
                    try {
                        Stat stat = zkClient.checkExists().forPath(servicePath);
                        if (stat == null || stat.getEphemeralOwner() != zkClient.getZookeeperClient().getZooKeeper().getSessionId()) {
                            System.exit(ExitStatus.ZOOKEEPER_CONNECTION_LOST.getCode());
                            return;
                        }
                    } catch (Exception e) {
                        log.error("zookeeper registry service error, path: {}, error: {}", servicePath, e.getMessage());
                        System.exit(ExitStatus.ZOOKEEPER_CONNECTION_LOST.getCode());
                        return;
                    }
                    log.info("zookeeper 连接恢复");
                }
                case LOST -> {
                    log.error("zookeeper 连接丢失，请检查网络连接!!");
                    System.exit(ExitStatus.ZOOKEEPER_CONNECTION_LOST.getCode());
                }
            }
        });
    }

    private record ServerInfo(String host, int tcpPort, int rpcPort) {
    }

    protected void stop() throws Exception {
        tcpServer.close();
        if (!tcpServer.awaitTermination(2, TimeUnit.MINUTES)) {
            log.error("tcp server close timeout");
        }
        synchronized (Players.class) {
            for (Player player : Players.getPlayers().values()) {
                try {
                    player.execute(player::destroy);
                    if (!player.awaitAllTaskComplete(20, TimeUnit.SECONDS)) {
                        log.error("player destroy timeout");
                    }
                } catch (Exception e) {
                    log.error("player destroy error", e);
                }
            }
        }
        grpcServer.shutdown();
        try {
            grpcServer.awaitTermination(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("grpc server awaitTermination error", e);
        }
        ThreadPool.close();
        SpringApplication.exit(applicationContext);
    }
}
