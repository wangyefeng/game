package org.game.proto;

import org.game.proto.struct.Common.PbInt;

public abstract class CommonPbUtil {

    public static PbInt parse(int val) {
        return PbInt.newBuilder().setVal(val).build();
    }
}
