package org.wyf.game.logic.player.task;

import org.wyf.game.logic.player.Player;
import org.wyf.game.logic.player.PlayerService;

public class LevelTaskStrategy extends NewProgressTaskStrategy<Integer> {

    @Override
    public long initProgress(Player player) {
        PlayerService playerService = player.getService(PlayerService.class);
        return playerService.getEntity().getLevel();
    }
}
