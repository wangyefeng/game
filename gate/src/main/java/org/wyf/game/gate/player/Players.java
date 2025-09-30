package org.wyf.game.gate.player;

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

    public static Player removePlayer(int id) {
        return players.remove(id);
    }

    public static Map<Integer, Player> getPlayers() {
        return players;
    }

    public static boolean containsPlayer(int id) {
        return players.containsKey(id);
    }
}
