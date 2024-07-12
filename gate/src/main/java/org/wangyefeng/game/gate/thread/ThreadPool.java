package org.wangyefeng.game.gate.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadPool {

    public static final int EXECUTOR_SIZE = 2;

    public static final ExecutorService[] executor = new ExecutorService[EXECUTOR_SIZE];

    static {
        for (int i = 0; i < EXECUTOR_SIZE; i++) {
            int finalI = i;
            executor[i] = Executors.newSingleThreadExecutor(r -> new Thread(r, "player-thread-" + finalI));
        }
    }

    public static ExecutorService getPlayerExecutor(int playerId) {
        return executor[playerId % EXECUTOR_SIZE];
    }
}
