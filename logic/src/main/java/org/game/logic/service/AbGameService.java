package org.game.logic.service;

import org.game.logic.player.Player;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public abstract class AbGameService implements GameService {

    private static String[] gameServices;

    protected Player player;

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPlayerId() {
        return player.getId();
    }

    public static GameService[] getGameServices(ApplicationContext applicationContext) {
        GameService[] result = new GameService[AbGameService.gameServices.length];
        int i = 0;
        for (String s : gameServices) {
            result[i++] = applicationContext.getBean(s, GameService.class);
        }
        return result;
    }
}
