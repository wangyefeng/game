package org.game.logic.player.task;

import org.game.logic.player.Player;

public interface TaskStrategy<Event> {

    long calculateProgress(Event event, TaskData taskData);

    long initProgress(Player player);
}
