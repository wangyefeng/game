package org.game.logic;

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

    private static boolean stopping = false;

    private static final String SERVICE_ROOT = "/logic";

    private void start() throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
                SpringApplication.exit(applicationContext);
            } catch (Exception e) {
                log.error("关闭服务器异常！", e);
            }
        }, "shutdown-hook"));
        initConfig();
        registerHandler();
        tcpServer.start();
        registerService();
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
        synchronized (Logic.class) {
            log.info("服务器关闭中，请等待...");
            stopping = true;
            tcpServer.close();
            ThreadPool.shutdown();
        }
    }

    @Override
    public void run(String... args) {
        try {
            start();
        } catch (Exception e) {
            throw new IllegalStateException("logic start error", e);
        }
    }

    private void registerHandler() {
        log.info("handler registering...");
        gateMsgHandlers.forEach(handler -> GateMsgHandler.register(handler));// 注册gate handler
        clientMsgHandlers.forEach(handler -> ClientMsgHandler.register(handler));// 注册client handler
        log.info("handler register end");
    }

    public static boolean isStopping() {
        return stopping;
    }

    public static void main(String[] args) {
        synchronized (Logic.class) {
            SpringApplication application = new SpringApplication(Logic.class);
            application.setRegisterShutdownHook(false);
            application.run(args);
            log.info("逻辑服务器启动成功！");
        }
    }
}
