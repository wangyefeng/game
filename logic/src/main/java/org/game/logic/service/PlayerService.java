package org.game.logic.service;

import org.game.common.util.JsonUtil;
import org.game.logic.entity.PlayerInfo;
import org.game.logic.repository.PlayerDao;
import org.game.proto.struct.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlayerService extends AbGameService {

    @Autowired
    private PlayerDao playerDao;

    private PlayerInfo playerInfo;

    @Override
    public void load() {
        playerInfo = playerDao.findById(player.getId()).orElseThrow();
    }

    @Override
    public void save() {
        playerDao.save(playerInfo);
    }

    @Override
    public void init(Login.PbRegister registerMsg) {
        playerInfo = new PlayerInfo(player.getId(), registerMsg.getName());
    }

    @Override
    public String dataToString() {
        return JsonUtil.toJson(playerInfo);
    }

    public boolean playerExists() {
        return playerDao.existsById(player.getId());
    }

}
