package org.wangyefeng.game.logic.player;

import org.wangyefeng.game.logic.data.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Players {

    private static final Map<Integer, Player> players = new ConcurrentHashMap<>();

    public static void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public static Player getPlayer(int id) {
        return players.get(id);
    }

    public static void removePlayer(int id) {
        players.remove(id);
    }

    public static Map<Integer, Player> getPlayers() {
        return players;
    }

    public static boolean containsPlayer(int id) {
        return players.containsKey(id);
    }
}
