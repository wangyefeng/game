package org.wangyefeng.game.gate.net;

/**
 * 漏桶算法实现
 */
public class LeakyBucket {
    private final long capacity;          // 桶的容量
    private final long leakRate;          // 漏出速率（单位时间内处理的请求数）
    private long water;                    // 当前桶中的水量（请求数）
    private long lastLeakTime;             // 上一次漏水时间

    public LeakyBucket(long capacity, long leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.water = 0;
        this.lastLeakTime = System.currentTimeMillis();
    }

    public boolean addRequest() {
        leak();  // 先漏水
        if (water < capacity) {
            water++;
            return true;  // 成功添加请求
        }
        return false;  // 桶满，丢弃请求
    }

    private void leak() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastLeakTime;

        // 计算漏出的水量
        long leaked = elapsed * leakRate / 1000; // 每秒处理 leakRate 个请求
        if (leaked > 0) {
            if (water - leaked > 0) {
                water -= leaked;
                lastLeakTime += leaked * 1000;
            } else {
                water = 0;
                lastLeakTime = now;
            }
        }
    }
}
