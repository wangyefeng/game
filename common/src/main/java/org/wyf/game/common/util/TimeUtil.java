package org.wyf.game.common.util;

import java.util.concurrent.TimeUnit;

public abstract class TimeUtil {

    private TimeUtil() {
        // prevent instantiation
    }

    public static final long MILLIS_TO_NANOS = TimeUnit.MILLISECONDS.toNanos(1);
}
