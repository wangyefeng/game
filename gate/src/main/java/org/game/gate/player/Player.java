package org.game.gate.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.gate.net.client.LogicClient;
import org.game.proto.MessageCode;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.protocol.GateToLogicProtocol;

import java.util.concurrent.ThreadPoolExecutor;

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
    private ThreadPoolExecutor executor;

    private LogicClient logicClient;

    public Player(int id, Channel channel, ThreadPoolExecutor executor, LogicClient logicClient) {
        this.id = id;
        this.channel = channel;
        this.executor = executor;
        this.logicClient = logicClient;
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

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return id + "";
    }

    public LogicClient getLogicClient() {
        return logicClient;
    }

    public void writeToClient(GateToClientProtocol protocol) {
        writeToClient(protocol, null);
    }

    public void writeToClient(GateToClientProtocol protocol, Message message) {
        channel.writeAndFlush(new MessageCode<>(protocol, message));
    }

    public void writeToLogic(GateToLogicProtocol protocol, Message message) {
        getLogicClient().getChannel().writeAndFlush(new MessageCode<>(protocol, message));
    }

    public void writeToLogic(GateToLogicProtocol protocol) {
        writeToLogic(protocol, null);
    }
}
