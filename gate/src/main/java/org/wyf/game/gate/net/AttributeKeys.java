package org.wyf.game.gate.net;

import io.netty.util.AttributeKey;
import org.wyf.game.gate.player.Player;

public abstract class AttributeKeys {

    public static final AttributeKey<Player> PLAYER = AttributeKey.valueOf("player");
}
