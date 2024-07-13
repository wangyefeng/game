package org.wangyefeng.game.gate;

import io.netty.util.ResourceLeakDetector;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
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
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

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

    private void start() throws Exception {
        globalSetting();
        registerHandler();
        ProtocolUtils.init();
        logicClient.start();
        tcpServer.start();
    }

    private void globalSetting() {
        // 设置netty的资源泄露检测
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        // 设置netty日志级别
        System.setProperty("io.netty.logging.level", "INFO");
        // 设置log4j异步日志
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        log.info("是否为异步日志：{}", AsyncLoggerContextSelector.isSelected());
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
