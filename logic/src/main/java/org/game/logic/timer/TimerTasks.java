package org.game.logic.timer;

import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时器集中管理
 *
 * @author 王叶峰
 */
@Component
public class TimerTasks {

    private static final Logger log = LoggerFactory.getLogger(TimerTasks.class);

    /**
     * 0点重置玩家数据
     */
    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void playerResetData() {
        log.info("0点重置数据");
        synchronized (Players.getPlayers()) {
            for (Player player : Players.getPlayers().values()) {
                player.execute(() -> player.dailyReset(true));
            }
        }
    }

    /**
     * 输出日志
     */
    @Async
    @Scheduled(cron = "*/10 * * * * ?")
    public void log() {
        log.info("在线玩家数量{}", Players.getPlayers().size());
    }

}
