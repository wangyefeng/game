package org.game.logic.service;

import org.game.logic.entity.PlayerInfo;
import org.game.logic.repository.PlayerRepository;
import org.game.proto.struct.Login;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlayerService extends AbstractGameService<PlayerInfo, PlayerRepository> {

    @Override
    public void register(Login.PbRegister registerMsg) {
        entity = new PlayerInfo(player.getId(), registerMsg.getName());
    }

    public boolean playerExists() {
        return repository.existsById(player.getId());
    }
}
