package org.game.proto;

import com.google.protobuf.Int32Value;

public abstract class CommonPbUtil {

    public static Int32Value parse(int val) {
        return Int32Value.newBuilder().setValue(val).build();
    }
}
