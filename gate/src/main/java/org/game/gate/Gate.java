package org.game.gate;

import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.core.util.Constants;
import org.game.gate.handler.client.ClientMsgHandler;
import org.game.gate.handler.logic.LogicMsgHandler;
import org.game.gate.net.TcpServer;
import org.game.gate.net.client.LogicClient;
import org.game.gate.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

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

    private static boolean stopping = false;

    @Autowired
    private ApplicationContext applicationContext;

    static {
        // 设置netty的资源泄露检测
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> log.error("未捕获异常！", e));
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        System.setProperty(Constants.LOG4J_CONTEXT_SELECTOR, "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }

    private void start() {
        addShutdownHook();
        registerHandler();
        logicClient.start();
        tcpServer.start();
        log.info("网关服务器启动成功！");
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (Gate.class) {
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
        }, "shutdown-hook"));
    }

    public void close() {
        stopping = true;
        try {
            tcpServer.close();
        } catch (Exception e) {
            log.error("关闭TCP服务器异常！", e);
        }
        try {
            logicClient.close();
        } catch (Exception e) {
            log.error("关闭逻辑服务器的连接异常！", e);
        }
        try {
            ThreadPool.shutdown();
        } catch (Exception e) {
            log.error("关闭业务线程池异常！", e);
        }
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
        synchronized (Gate.class) {
            SpringApplication application = new SpringApplication(Gate.class);
            application.setRegisterShutdownHook(false);
            application.run(args);
        }
    }

    public static boolean isStopping() {
        return stopping;
    }
}
