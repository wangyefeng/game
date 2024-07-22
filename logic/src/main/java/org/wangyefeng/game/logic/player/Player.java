package org.wangyefeng.game.logic.player;

import io.netty.channel.Channel;
import org.wangyefeng.game.logic.data.PlayerInfo;

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
}
