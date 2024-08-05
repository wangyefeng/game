package org.wangyefeng.game.gate;

import io.netty.util.ResourceLeakDetector;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wangyefeng.game.gate.handler.client.ClientMsgHandler;
import org.wangyefeng.game.gate.handler.logic.LogicMsgHandler;
import org.wangyefeng.game.gate.net.TcpServer;
import org.wangyefeng.game.gate.net.client.LogicClient;

import java.util.Collection;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 网关服务器
 */
@SpringBootApplication
public class Gate implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Gate.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private LogicClient logicClient;

    @Autowired
    private Collection<LogicMsgHandler<?>> logicMsgHandlers;

    @Autowired
    private Collection<ClientMsgHandler<?>> clientMsgHandlers;

    static {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    private void start() throws Exception {
        registerHandler();
        logicClient.start();
        tcpServer.start();
    }

    @PreDestroy
    public void close() {
        log.info("gate server closed");
    }

    @Override
    public void run(String... args) {
        try {
            start();
        } catch (Exception e) {
            log.error("Gate start error", e);
            System.exit(1);
        }
    }

    private void registerHandler() {
        log.info("handler registering...");
        logicMsgHandlers.forEach(LogicMsgHandler::register);
        clientMsgHandlers.forEach(ClientMsgHandler::register);
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication.run(Gate.class, args);
    }
}
