package org.game.logic.player;

import java.time.LocalDate;

public interface DailyReset {

    void reset(LocalDate resetDate, boolean isSend);
}
