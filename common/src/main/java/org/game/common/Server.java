package org.game.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public abstract class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    protected volatile static Status status;

    static {
        // 设置默认的异常处理器, 打印日志
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> log.error("未捕获异常！", e));
    }

    @EventListener(ApplicationStartedEvent.class)
    protected void start() {
        try {
            status = Status.STARTING;
            start0();
            afterStart();
            status = Status.RUNNING;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    log.info("JVM 正在关闭，请等待...");
                    status = Status.STOPPING;
                    stop();
                } catch (Exception e) {
                    log.error("关闭服务器异常！", e);
                } finally {
                    log.info("JVM 已关闭！");
                }
            }, "shutdown-hook"));
            log.info("服务器启动成功！");
        } catch (Exception e) {
            log.error("服务器启动失败！", e);
            status = Status.STOPPING;
            System.exit(-1);
        }

    }

    protected abstract void start0() throws Exception;

    protected abstract void afterStart() throws Exception;

    protected abstract void stop() throws Exception;

    /**
     * 服务器当前状态
     */
    protected enum Status {
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

    public static boolean isStopping() {
        return status == Status.STOPPING;
    }
}
