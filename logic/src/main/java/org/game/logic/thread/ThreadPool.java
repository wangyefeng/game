package org.game.logic.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadPool {

    private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);

    public static final int EXECUTOR_SIZE = Runtime.getRuntime().availableProcessors();

    public static final ThreadPoolExecutor[] playerExecutors = new ThreadPoolExecutor[EXECUTOR_SIZE];

    public static final ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(5);

    static {
        for (int i = 0; i < EXECUTOR_SIZE; i++) {
            final int k = i;
            playerExecutors[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "player-thread-" + k));
        }
    }

    public static ThreadPoolExecutor getPlayerExecutor(int playerId) {
        return playerExecutors[playerId % EXECUTOR_SIZE];
    }

    public static void shutdown() {
        for (ThreadPoolExecutor executor : ThreadPool.playerExecutors) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                log.error("player thread {} shutdown interrupted", e);
            }
        }
        log.info("player thread pool shutdown");
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long delay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(runnable, delay, period, unit);
    }
}
