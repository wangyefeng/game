package org.game.common;

public abstract class RedisKeys {

    public static final String PLAYER_TOKEN_PREFIX = "player_token:";

    public static abstract class Locks {

        public static final String ACCOUNT_INNER_LOCK = "account_inner_lock";
    }
}
