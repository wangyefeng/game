package org.game.logic.timer;

import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.game.logic.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时器集中管理
 * 
 * @author 王叶峰
 *
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
		for (Player player : Players.getPlayers().values()) {
			ThreadPool.executePlayerAction(player.getId(), () -> player.dailyReset(true));
		}
	}

}
