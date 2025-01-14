package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.EmptyArrays;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.game.common.Server;
import org.game.config.tools.Tool;
import org.game.logic.net.TcpServer;
import org.game.logic.player.activity.TimeIntervalManager;
import org.game.logic.thread.ThreadPool;
import org.game.proto.MsgHandler;
import org.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"org.game.config", "org.game.logic"}, excludeFilters = @Filter(type = FilterType.ANNOTATION, value = Tool.class))
public class Logic extends Server {

    private static final Logger log = LoggerFactory.getLogger(Logic.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${zookeeper.root-path}")
    private String rootPath;

    @Value("${logic.server-id}")
    private int id;

    @Autowired
    private io.grpc.Server grpcServer;

    @Autowired
    private TimeIntervalManager timeIntervalManager;

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
        Protocols.init();
        registerHandler();
        initGameService();
        ThreadPool.start();
        timeIntervalManager.init();
    }

    private void initGameService() {
        // 主动触发GameService的初始化，防止延迟初始化多线程安全问题
        applicationContext.getBeansOfType(GameService.class);
    }

    @Override
    protected void afterStart() throws Exception {
        tcpServer.start();
        grpcServer.start();
        registerService();
    }

    private void registerService() throws Exception {
        if (zkClient.checkExists().forPath(rootPath) == null) {
            zkClient.create().forPath(rootPath, EmptyArrays.EMPTY_BYTES);
        }
        String servicePath = rootPath + "/" + id;
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath, (tcpServer.getHost() + ":" + tcpServer.getPort()).getBytes());
        log.info("zookeeper registry service success, path: {}", servicePath);
    }

    protected void stop() throws Exception {
        tcpServer.close();
        grpcServer.shutdown();
        ThreadPool.shutdown();
        SpringApplication.exit(applicationContext);
    }

    private void registerHandler() {
        log.info("handler registering...");
        applicationContext.getBeansOfType(MsgHandler.class).values().forEach(MsgHandler::register);// 注册所有handler
        log.info("handler register end");
    }
}
