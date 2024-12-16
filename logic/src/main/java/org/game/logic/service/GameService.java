package org.game.logic.service;

import org.game.logic.player.Player;
import org.game.proto.struct.Login;
import org.springframework.stereotype.Service;

/**
 * 玩家服务接口
 *
 * @author 王叶峰
 */
@Service
public interface GameService {

    void load();

    void save();

    void setPlayer(Player player);

    void init(Login.PbRegister registerMsg);

    String dataToString();
}
