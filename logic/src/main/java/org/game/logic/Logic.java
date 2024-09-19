package org.game.logic;

import jakarta.annotation.PreDestroy;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.game.logic.handler.ClientMsgHandler;
import org.game.logic.handler.GateMsgHandler;
import org.game.logic.net.TcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

    private static boolean stopping = false;

    private static final String SERVICE_ROOT = "/logic";

    private void start() throws Exception {
        registerHandler();
        tcpServer.start();
        registerService();
    }

    private void registerService() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(SERVICE_ROOT, false) == null) {
            zooKeeper.create(SERVICE_ROOT, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String servicePath = SERVICE_ROOT + "/" + tcpServer.getHost() + ":" + tcpServer.getPort();
        String path = zooKeeper.create(servicePath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        log.info("zookeeper registry service success, path: {}", path);
    }

    @PreDestroy
    public void close() throws Exception {
        stopping = true;
        log.info("服务器关闭中，请等待...");
        tcpServer.close(true);
    }

    @Override
    public void run(String... args) {
        try {
            start();
            log.info("逻辑服务器启动成功！");
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
        SpringApplication.run(Logic.class, args);
    }
}
