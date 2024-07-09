package org.wangyefeng.game.gate.player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Players {

    private static final Map<Integer, Player> players = new HashMap<>();

    public static final Lock lock = new ReentrantLock();

    public static boolean addPlayer(Player player) {
        return player == players.putIfAbsent(player.getId(), player);
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
