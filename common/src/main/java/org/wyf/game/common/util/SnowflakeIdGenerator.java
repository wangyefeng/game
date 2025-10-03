package org.wyf.game.common.util;

/**
 * 雪花算法生成id
 */
public class SnowflakeIdGenerator {

    // 起始时间戳常数（北京时间：2025-01-01 00:00:00）
    private final static long twEpoch = 1735660800000L;

    // 机器ID位数
    private final static long workerIdBits = 5L;
    private final static long datacenterIdBits = 5L;

    // 最大机器ID
    private final static long maxWorkerId = ~(-1L << workerIdBits);
    private final static long maxDatacenterId = ~(-1L << datacenterIdBits);

    // 序列号位数
    private final static long sequenceBits = 12L;

    // 移位
    private final static long workerIdShift = sequenceBits;
    private final static long datacenterIdShift = sequenceBits + workerIdBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 序列号掩码
    private final static long sequenceMask = ~(-1L << sequenceBits);

    // 工作参数
    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID 必须在 0 和 " + maxWorkerId + " 之间");
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID 必须在 0 和 " + maxDatacenterId + " 之间");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        // 时钟回拨处理
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常");
        }

        // 同一毫秒内生成
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 序列号用完，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 组合生成ID
        return ((timestamp - twEpoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}