package org.wyf.game.logic.player;

import java.util.HashMap;
import java.util.Map;

public abstract class Players {

    private static final Map<Integer, Player> players = new HashMap<>();

    public static synchronized void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public static synchronized Player getPlayer(int id) {
        return players.get(id);
    }

    public static synchronized void removePlayer(int id) {
        players.remove(id);
    }

    public static synchronized Map<Integer, Player> getPlayers() {
        return players;
    }

    public static synchronized boolean containsPlayer(int id) {
        return players.containsKey(id);
    }
}
