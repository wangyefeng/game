package org.wangyefeng.game.logic;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.wangyefeng.game.logic.config.GlobalConfig;
import org.wangyefeng.game.logic.handler.Handler;
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
    private Collection<Handler<?>> handlers;

    private void start() throws Exception {
        registerHandler();
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
        handlers.forEach(handler -> Handler.register(handler));
        log.info("handler register end");
    }

    public static void main(String[] args) {
        SpringApplication.run(Logic.class, args);
    }
}
