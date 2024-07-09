package org.wangyefeng.game.gate.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadPool {

    public static final int EXECUTOR_SIZE = 2;

    public static final ExecutorService[] executor = new ExecutorService[EXECUTOR_SIZE];

    public static int CLIENT_EXECUTOR_INDEX = 0;

    static {
        for (int i = 0; i < EXECUTOR_SIZE; i++) {
            int finalI = i;
            executor[i] = Executors.newSingleThreadExecutor(r -> new Thread(r, "player-thread-" + finalI));
        }
    }

    public static ExecutorService next() {
        return executor[CLIENT_EXECUTOR_INDEX++ % EXECUTOR_SIZE];
    }
}
