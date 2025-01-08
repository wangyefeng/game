package org.game.gate.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadPool {

    private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);

    public static final int EXECUTOR_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    public static final ThreadPoolExecutor[] executor = new ThreadPoolExecutor[EXECUTOR_SIZE];

    public static void start() {
        for (int i = 0; i < EXECUTOR_SIZE; i++) {
            int finalI = i;
            executor[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "player-thread-" + finalI));
        }
        log.info("线程池初始化完成！");
    }

    public static ThreadPoolExecutor getPlayerExecutor(int playerId) {
        return executor[playerId % EXECUTOR_SIZE];
    }

    public static void shutdown() {
        for (ThreadPoolExecutor executor : executor) {
            executor.close();
        }
        log.info("线程池已关闭！");
    }
}
