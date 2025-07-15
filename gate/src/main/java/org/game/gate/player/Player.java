package org.game.gate.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.gate.net.client.LogicClient;
import org.game.proto.MessageCode;
import org.game.proto.MessagePlayer;
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
    private final ThreadPoolExecutor executor;

    private final LogicClient logicClient;

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

    public LogicClient getLogicClient() {
        return logicClient;
    }

    public void writeToClient(GateToClientProtocol protocol) {
        writeToClient(MessageCode.of(protocol));
    }

    public void writeToClient(GateToClientProtocol protocol, Message data) {
        writeToClient(MessageCode.of(protocol, data));
    }

    private void writeToClient(MessageCode<? extends Message> message) {
        channel.writeAndFlush(message);
    }

    public void writeToLogic(GateToLogicProtocol protocol) {
        writeToLogic(MessageCode.of(protocol));
    }

    public void writeToLogic(GateToLogicProtocol protocol, Message data) {
        writeToLogic(MessageCode.of(protocol, data));
    }

    private void writeToLogic(MessageCode<? extends Message> message) {
        getLogicClient().getChannel().writeAndFlush(message);
    }

    public void writeToLogic(GateToLogicProtocol protocol, int playerId, Message data) {
        writeToLogic(MessagePlayer.of(playerId, protocol, data));
    }

    public void writeToLogic(GateToLogicProtocol protocol, int playerId) {
        writeToLogic(MessagePlayer.of(playerId, protocol));
    }

    private void writeToLogic(MessagePlayer<? extends Message> message) {
        getLogicClient().getChannel().writeAndFlush(message);
    }
}
