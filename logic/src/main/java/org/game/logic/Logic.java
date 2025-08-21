package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.EmptyArrays;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.game.common.Server;
import org.game.common.util.JsonUtil;
import org.game.config.tools.Tool;
import org.game.logic.actor.PlayerActorService;
import org.game.logic.net.TcpServer;
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
    private PlayerActorService playerActorService;

    @Autowired
    private SpringConfig springConfig;

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
    protected void start0() {
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

    private void registerZkService() throws Exception {
        String rootPath = springConfig.getRootPath();
        if (zkClient.checkExists().forPath(rootPath) == null) {
            zkClient.create().forPath(rootPath, EmptyArrays.EMPTY_BYTES);
        }
        String servicePath = rootPath + "/" + springConfig.getLogicId();
        if (zkClient.checkExists().forPath(servicePath) == null) {
            zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath, (JsonUtil.toJson(new ServerInfo(springConfig.getHost(), tcpServer.getPort(), grpcServer.getPort()))).getBytes());
            log.info("zookeeper registry service success, path: {}", servicePath);
        }
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

    private record ServerInfo(String host, int tcpPort, int rpcPort) {
    }

    protected void stop() throws Exception {
        tcpServer.close();
        grpcServer.shutdown();
        playerActorService.close();
        ThreadPool.shutdown();
        SpringApplication.exit(applicationContext);
    }
}
