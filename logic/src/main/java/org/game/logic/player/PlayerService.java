package org.game.logic.player;

import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    public void logout(Player player) {
        Players.removePlayer(player.getId());
    }

}
