package org.wyf.game.common;

import javax.management.timer.Timer;

public abstract class GlobalConstant {

    private GlobalConstant() {
    }

    /**
     * 玩家token密钥
     */
    public static final String PLAYER_TOKEN_SECRET_KEY = "365zb5t3e4vb65%$#2390nb";

    /**
     * 玩家token过期时间
     */
    public static final long PLAYER_TOKEN_EXPIRE_TIME = 30 * Timer.ONE_DAY;
}
