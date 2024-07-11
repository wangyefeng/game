package org.wangyefeng.game.gate.player;

import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;

/**
 * 玩家对象
 * @date 2024-07-09
 * @author wangyefeng
 */
public class Player {

    private final int id;

    private Channel channel;

    private long lastLoginTime;

    /**
     * 绑定的业务线程
     */
    private ExecutorService executor;

    public Player(int id, Channel channel, ExecutorService executor) {
        this.id = id;
        this.channel = channel;
        this.executor = executor;
    }

    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return id + "";
    }
}