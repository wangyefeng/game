package org.game.logic.service;

import org.game.logic.player.Player;
import org.springframework.stereotype.Service;

@Service
public abstract class AbGameService implements GameService {

    protected Player player;

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }
}
