package org.game.common;

import org.apache.logging.log4j.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    protected static Status status;

    static {
        // 设置默认的异常处理器, 打印日志
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> log.error("未捕获异常！", e));
        // 设置全局异步日志处理器
        System.setProperty(Constants.LOG4J_CONTEXT_SELECTOR, "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }

    protected void start(String[] args) {
        try {
            status = Status.STARTING;
            start0(args);
            afterStart();
            status = Status.RUNNING;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    log.info("JVM 正在关闭，请等待...");
                    status = Status.STOPPING;
                    stop();
                } catch (Exception e) {
                    log.error("关闭服务器异常！", e);
                    System.exit(1);
                }
                log.info("JVM 已关闭！");
            }, "shutdown-hook"));
            log.info("服务器启动成功！");
        } catch (Exception e) {
            log.error("服务器启动失败！", e);
            status = Status.STOPPING;
            System.exit(-1);
        }

    }

    protected abstract void start0(String[] args) throws Exception;

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
