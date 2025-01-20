package org.game.logic.net;

import io.netty.util.AttributeKey;

import java.util.List;

public class ChannelKeys {
    public static final AttributeKey<List<Integer>> PLAYERS_KEY = AttributeKey.valueOf("players");
}
