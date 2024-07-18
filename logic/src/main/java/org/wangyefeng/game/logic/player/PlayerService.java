package org.wangyefeng.game.logic.player;

import org.springframework.stereotype.Service;
import org.wangyefeng.game.logic.data.Player;

@Service
public class PlayerService {

    public void logout(Player player) {
        Players.removePlayer(player.getId());
    }

}
