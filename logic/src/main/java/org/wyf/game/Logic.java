package org.wyf.game;

import io.netty.util.ResourceLeakDetector;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.wyf.game.proto.protocol.Protocols;
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

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class Logic extends Server {

    private static final Logger log = LoggerFactory.getLogger(Logic.class);

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
        SpringApplication application = new SpringApplication(Logic.class);
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
        String servicePath = zookeeperProperties.rootPath() + "/" + logicConfig.serverId();
        try {
            zkClient.delete().forPath(servicePath);
        } catch (Exception e) {
            // ignore
        }
        byte[] data = (JsonUtil.toJson(new ServerInfo(logicConfig.host(), tcpServer.getPort(), grpcServer.getPort()))).getBytes();
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(servicePath, data);
        log.info("zookeeper registry service success, path: {}", servicePath);
    }

    private void addZkListener() {
        zkClient.getConnectionStateListenable().addListener((_, state) -> {
            switch (state) {
                case SUSPENDED -> log.warn("zookeeper 连接断开，等待重连...");
                case RECONNECTED -> {
                    log.info("zookeeper 断线重连成功，开始恢复业务...");
                    try {
                        registerZkService();
                    } catch (Exception e) {
                        log.error("zookeeper 注册服务失败", e);
                    }
                }
                case LOST -> {
                    log.error("zookeeper 连接丢失，请检查网络连接...");
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
