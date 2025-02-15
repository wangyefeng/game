package org.game.logic;

import org.game.logic.entity.Entity;
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

    /**
     * 加载游戏数据
     */
    void load();

    /**
     * 初始化游戏数据
     */
    default void init() {}

    /**
     * 初始化完成后调用
     */
    default void afterInit() {}

    /**
     * 保存游戏数据
     */
    void save();

    /**
     * 获取实体对象
     *
     * @return 实体对象
     */
    E getEntity();

    /**
     * 异步保存游戏数据
     */
    void asyncSave();

    /**
     * 设置玩家对象
     *
     * @param player 玩家对象
     */
    void setPlayer(Player player);

    /**
     * 注册
     *
     * @param registerMsg 注册消息
     */
    void register(Login.PbRegisterReq registerMsg);

    /**
     * 登录响应
     *
     * @param loginResp 登录响应消息
     */
    void loginResp(Login.PbLoginResp.Builder loginResp);
}
