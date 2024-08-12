package org.game.logic.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadPool {

    public static final int EXECUTOR_SIZE = Runtime.getRuntime().availableProcessors();

    public static final ThreadPoolExecutor[] executor = new ThreadPoolExecutor[EXECUTOR_SIZE];

    static {
        for (int i = 0; i < EXECUTOR_SIZE; i++) {
            int finalI = i;
            executor[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "player-thread-" + finalI));
        }
    }

    public static ThreadPoolExecutor getPlayerExecutor(int playerId) {
        return executor[playerId % EXECUTOR_SIZE];
    }
}
