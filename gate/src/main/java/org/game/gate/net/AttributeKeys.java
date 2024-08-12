package org.game.gate.net;

import io.netty.util.AttributeKey;
import org.game.gate.player.Player;

public abstract class AttributeKeys {

    public static final AttributeKey<Player> PLAYER = AttributeKey.valueOf("player");
}
