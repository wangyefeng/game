package org.game.logic.player.task;

public interface Task {

    long getProgress();

    void setProgress(long progress);

    boolean isFinished();

    void setFinished(boolean finished);
}
