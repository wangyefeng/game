package org.wangyefeng.game.logic;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wangyefeng.game.logic.config.GlobalConfig;
import org.wangyefeng.game.logic.handler.ClientHandler;
import org.wangyefeng.game.logic.handler.GateHandler;
import org.wangyefeng.game.logic.net.TcpServer;

import java.util.Collection;

@SpringBootApplication
public class Logic implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Logic.class);

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private GlobalConfig config;

    @Autowired
    private Collection<GateHandler<?>> gateHandlers;

    @Autowired
    private Collection<ClientHandler<?>> clientHandlers;

    private void start() throws Exception {
        registerHandler();
        tcpServer.start(config.getTcpPort());
    }

    @PreDestroy
    public void close() {
        log.info("logic server closed");
    }

    @Override
    public void run(String... args) {
        try {
            start();
        } catch (Exception e) {
            log.error("logic start error", e);
            System.exit(1);
        }
    }

    private void registerHandler() {
        log.info("handler registering...");
        gateHandlers.forEach(handler -> GateHandler.register(handler));// 注册gate handler
        clientHandlers.forEach(handler -> ClientHandler.register(handler));// 注册client handler
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication.run(Logic.class, args);
    }
}
