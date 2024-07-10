package org.wangyefeng.game.gate;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wangyefeng.game.gate.config.GlobalConfig;
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
    private GlobalConfig config;

    @Autowired
    private Collection<LogicMsgHandler<?>> logicMsgHandlers;

    @Autowired
    private Collection<ClientMsgHandler<?>> clientMsgHandlers;

    private void start() throws Exception {
        registerHandler();
        logicClient.start();
        tcpServer.start(config.getTcpPort());
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
        logicMsgHandlers.forEach(logicMsgHandler -> LogicMsgHandler.register(logicMsgHandler));
        clientMsgHandlers.forEach(clientMsgHandler -> ClientMsgHandler.register(clientMsgHandler));
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication.run(Gate.class, args);
    }
}
