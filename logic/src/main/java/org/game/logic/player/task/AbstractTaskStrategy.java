package org.game.logic.player.task;

import org.game.logic.player.Player;

public abstract class AbstractTaskStrategy<Event> implements TaskStrategy<Event> {

    @Override
    public long initProgress(Player player) {
        return 0;
    }
}
