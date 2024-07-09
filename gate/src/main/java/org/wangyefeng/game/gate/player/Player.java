package org.wangyefeng.game.gate.player;

import io.netty.channel.Channel;

import java.util.concurrent.Executor;

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
    private Executor executor;

    public Player(int id, Channel channel) {
        this.id = id;
        this.channel = channel;
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
}
