package org.wangyefeng.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.wangyefeng.game.logic.data.PlayerInfo;
import org.wangyefeng.game.proto.MessagePlayer;
import org.wangyefeng.game.proto.protocol.LogicToClientProtocol;

public class Player {

    private PlayerInfo playerInfo;

    private Channel channel;

    public Player(PlayerInfo playerInfo, Channel channel) {
        this.playerInfo = playerInfo;
        this.channel = channel;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getId() {
        return playerInfo.getId();
    }

    public void sendToClient(LogicToClientProtocol protocol, Message message) {
        channel.writeAndFlush(new MessagePlayer<>(getId(), protocol, message));
    }

    public void sendToClient(LogicToClientProtocol protocol) {
        channel.writeAndFlush(new MessagePlayer<>(getId(), protocol));
    }
}
