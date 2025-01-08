package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.EmptyArrays;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.game.common.Server;
import org.game.config.Configs;
import org.game.config.service.CfgService;
import org.game.config.tools.MysqlToExcel;
import org.game.config.tools.XlsxToSql;
import org.game.logic.net.ClientMsgHandler;
import org.game.logic.net.GateMsgHandler;
import org.game.logic.net.TcpServer;
import org.game.logic.thread.ThreadPool;
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

import java.util.Collection;

@SpringBootApplication
@ComponentScan(basePackages = {"org.game.config", "org.game.logic"}, excludeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, value = XlsxToSql.class), @Filter(type = FilterType.ASSIGNABLE_TYPE, value = MysqlToExcel.class)})
public class Logic extends Server {

    private static final Logger log = LoggerFactory.getLogger(Logic.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private Collection<GateMsgHandler<?>> gateMsgHandlers;

    @Autowired
    private Collection<ClientMsgHandler<?>> clientMsgHandlers;

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

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    /**
     * 初始化spring容器后，启动服务器
     */
    @Override
    protected void start0() throws Exception {
        Protocols.init();
        registerHandler();
        tcpServer.start();
        initGameService();
        ThreadPool.start();
    }

    private void initGameService() {
        // 主动触发GameService的初始化，防止延迟初始化多线程安全问题
        applicationContext.getBeansOfType(GameService.class);
    }

    @Override
    protected void afterStart() throws Exception {
        grpcServer.start();
        registerService();
    }

    private void initConfig() {
        log.info("开始加载配置表...");
        long start = System.currentTimeMillis();
        Configs.init(applicationContext.getBeansOfType(CfgService.class).values());
        log.info("加载配置完成, 花费: {}毫秒", System.currentTimeMillis() - start);
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
        gateMsgHandlers.forEach(GateMsgHandler::register);// 注册gate handler
        clientMsgHandlers.forEach(ClientMsgHandler::register);// 注册client handler
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Logic.class);
        application.setRegisterShutdownHook(false);// 关闭Spring-boot停服处理策略
        application.run(args);
    }

}
