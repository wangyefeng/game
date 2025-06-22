package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.EmptyArrays;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.bson.Document;
import org.game.common.Server;
import org.game.common.util.JsonUtil;
import org.game.config.tools.Tool;
import org.game.logic.net.TcpServer;
import org.game.logic.player.function.TimeIntervalManager;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

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

    @Value("${zookeeper.root-path}")
    private String rootPath;

    @Value("${logic.server-id}")
    private int id;

    @Value("${logic.host}")
    private String host;

    @Autowired
    private io.grpc.Server grpcServer;

    @Autowired
    private TimeIntervalManager timeIntervalManager;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private List<MsgHandler<?>> msgHandlers;

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
        checkMongo();
        Protocols.init();
        registerHandler();
        initGameService();
        ThreadPool.start();
        timeIntervalManager.init();
    }

    /**
     * 检查mongo是否可用
     */
    public void checkMongo() {
        Document ping = mongoTemplate.executeCommand("{ping:1}");
        if (ping.getDouble("ok") != 1.0) {
            throw new RuntimeException("MongoDB not available");
        }
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
        zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath, (JsonUtil.toJson(new ServerInfo(host, tcpServer.getPort(), grpcServer.getPort()))).getBytes());
        log.info("zookeeper registry service success, path: {}", servicePath);
    }

    private record ServerInfo(String host, int tcpPort, int rpcPort) {}

    protected void stop() throws Exception {
        tcpServer.close();
        grpcServer.shutdown();
        ThreadPool.shutdown();
        SpringApplication.exit(applicationContext);
    }

    private void registerHandler() {
        log.info("handler registering...");
        msgHandlers.forEach(MsgHandler::register);// 注册所有handler
        log.info("handler register end");
    }
}
