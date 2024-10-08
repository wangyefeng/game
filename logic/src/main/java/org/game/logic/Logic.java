package org.game.logic;

import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.game.logic.data.config.CfgService;
import org.game.logic.data.config.Config;
import org.game.logic.handler.ClientMsgHandler;
import org.game.logic.handler.GateMsgHandler;
import org.game.logic.net.TcpServer;
import org.game.logic.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

@SpringBootApplication
public class Logic implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Logic.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private Collection<GateMsgHandler<?>> gateMsgHandlers;

    @Autowired
    private Collection<ClientMsgHandler<?>> clientMsgHandlers;

    @Autowired
    private ZooKeeper zooKeeper;

    @Autowired
    private ApplicationContext applicationContext;

    private Status status = Status.STARTING;

    private static final String SERVICE_ROOT = "/logic";

    static {
        // 设置netty的资源泄露检测
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> log.error("未捕获异常！", e));
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        System.setProperty(Constants.LOG4J_CONTEXT_SELECTOR, "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }

    /**
     * 初始化spring容器后，启动服务器
     *
     * @throws Exception 异常
     */
    private void start() throws Exception {
        addShutdownHook();
        initConfig();
        registerHandler();
        tcpServer.start();
        registerService();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (Logic.class) {
                if (status == Status.RUNNING) {
                    try {
                        log.info("JVM 正在关闭，请等待...");
                        close();
                    } catch (Exception e) {
                        log.error("关闭服务器异常！", e);
                    } finally {
                        SpringApplication.exit(applicationContext);
                    }
                    log.info("JVM 已关闭！");
                }
            }
        }, "shutdown-hook"));
    }

    private void initConfig() {
        Collection<CfgService> cfgServices = applicationContext.getBeansOfType(CfgService.class).values();
        cfgServices.forEach(CfgService::init);
        Config.reload(cfgServices);
    }

    public void reloadConfig() {
        initConfig();
    }

    private void registerService() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(SERVICE_ROOT, false) == null) {
            zooKeeper.create(SERVICE_ROOT, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String servicePath = SERVICE_ROOT + "/" + tcpServer.getHost() + ":" + tcpServer.getPort();
        String path = zooKeeper.create(servicePath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        log.info("zookeeper registry service success, path: {}", path);
    }

    public void close() throws Exception {
        status = Status.STOPPING;
        tcpServer.close();
        ThreadPool.shutdown();
    }

    @Override
    public void run(String... args) {
        try {
            start();
            status = Status.RUNNING;
        } catch (Exception e) {
            throw new IllegalStateException("logic start error", e);
        }
    }

    private void registerHandler() {
        log.info("handler registering...");
        gateMsgHandlers.forEach(GateMsgHandler::register);// 注册gate handler
        clientMsgHandlers.forEach(ClientMsgHandler::register);// 注册client handler
        log.info("handler register end");
    }

    public boolean isStopping() {
        return status == Status.STOPPING;
    }

    public static void main(String[] args) {
        synchronized (Logic.class) {
            SpringApplication application = new SpringApplication(Logic.class);
            application.setRegisterShutdownHook(false);
            application.run(args);
            log.info("逻辑服务器启动成功！");
        }
    }

    private enum Status {
        /**
         * 服务器启动中
         */
        STARTING,

        /**
         * 服务器运行中
         */
        RUNNING,

        /**
         * 服务器停止中
         */
        STOPPING,
    }
}
