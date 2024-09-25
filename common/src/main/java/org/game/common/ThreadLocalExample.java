package org.game.common;

public class ThreadLocalExample {

    // 创建一个 ThreadLocal 变量
    private static ThreadLocal<Integer> threadLocalValue = ThreadLocal.withInitial(() -> 1);

    public static void main(String[] args) {
        // 创建多个线程
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                threadLocalValue.set(threadLocalValue.get() + 1);
                System.out.println("Thread 1: " + threadLocalValue.get());
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                threadLocalValue.set(threadLocalValue.get() + 2);
                System.out.println("Thread 2: " + threadLocalValue.get());
            }
        });

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待线程完成
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
