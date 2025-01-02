package org.game.logic;

import org.game.logic.player.Player;
import org.game.proto.struct.Login;
import org.springframework.stereotype.Service;

/**
 * 玩家服务接口
 *
 * @author 王叶峰
 */
@Service
public interface GameService<E extends Entity> {

    void load();

    void save();

    void asyncSave();

    void setPlayer(Player player);

    void login(Login.PbLoginReq loginMsg);

    void register(Login.PbRegisterReq registerMsg);

    void loginResp(Login.PbLoginResp.Builder loginResp);

    E getEntity();
}
