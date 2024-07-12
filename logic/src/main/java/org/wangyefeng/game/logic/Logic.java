package org.wangyefeng.game.logic;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wangyefeng.game.logic.handler.ClientMsgHandler;
import org.wangyefeng.game.logic.handler.GateMsgHandler;
import org.wangyefeng.game.logic.net.TcpServer;
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

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

    private void start() throws Exception {
        registerHandler();
        ProtocolUtils.init();
        tcpServer.start();
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
        gateMsgHandlers.forEach(handler -> GateMsgHandler.register(handler));// 注册gate handler
        clientMsgHandlers.forEach(handler -> ClientMsgHandler.register(handler));// 注册client handler
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication.run(Logic.class, args);
    }
}
