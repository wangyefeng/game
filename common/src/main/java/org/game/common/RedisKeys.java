package org.game.common;

public abstract class RedisKeys {

    public static final String PLAYER_TOKEN_PREFIX = "player_token:";

    public static final String PLAYER_INFO = "player_info";

    public static abstract class Locks {

        public static final String ACCOUNT_LOCK_PREFIX = "account_lock:";

        public static final String TOKEN_LOCK_PREFIX = "token_lock:";
    }
}
