package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.EmptyArrays;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.game.common.Server;
import org.game.common.util.JsonUtil;
import org.game.config.service.ConfigService;
import org.game.config.tools.Tool;
import org.game.logic.net.TcpServer;
import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.game.logic.player.function.TimeIntervalManager;
import org.game.logic.thread.ThreadPool;
import org.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ComponentScan(basePackages = {"org.game.config", "org.game.logic"}, excludeFilters = @Filter(type = FilterType.ANNOTATION, value = Tool.class))
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
    private SpringConfig springConfig;

    @Autowired
    private ConfigService configService;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    public static void main(String[] args) {
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
        registerZkService(false);
    }

    /**
     * 注册zookeeper服务
     * @param isReconnect 是否是重连
     * @throws Exception 异常
     */
    private void registerZkService(boolean isReconnect) throws Exception {
        String rootPath = springConfig.getRootPath();
        if (zkClient.checkExists().forPath(rootPath) == null) {
            zkClient.create().forPath(rootPath, EmptyArrays.EMPTY_BYTES);
        }
        String servicePath = rootPath + "/" + springConfig.getLogicId();
        if (isReconnect) {
            try {
                zkClient.delete().forPath(servicePath);
            } catch (Exception e) {
                // ignore
            }
        }
        byte[] data = (JsonUtil.toJson(new ServerInfo(springConfig.getHost(), tcpServer.getPort(), grpcServer.getPort()))).getBytes();
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath, data);
        log.info("zookeeper registry service success, path: {}", servicePath);
    }

    private void addZkListener() {
        zkClient.getConnectionStateListenable().addListener((_, state) -> {
            switch (state) {
                case RECONNECTED:
                    log.info("zookeeper 断线重连成功，开始恢复业务...");
                    try {
                        registerZkService(true);
                    } catch (Exception e) {
                        log.error("zookeeper 注册服务失败", e);
                    }
                    break;
                case SUSPENDED:
                    log.warn("zookeeper 连接断开，等待重连...");
                    break;
                default:
                    break;
            }
        });
    }

    private record ServerInfo(String host, int tcpPort, int rpcPort) {
    }

    protected void stop() throws Exception {
        tcpServer.close();
        synchronized (Players.class) {
            for (Player player : Players.getPlayers().values()) {
                player.execute(player::destroy);
            }
        }
        grpcServer.shutdown();
        try {
            grpcServer.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("grpc server awaitTermination error", e);
        }
        ThreadPool.shutdown();
        SpringApplication.exit(applicationContext);
    }
}
