package org.wyf.game.logic.player;

import org.wyf.game.common.util.JsonUtil;
import org.wyf.game.logic.database.Repository;
import org.wyf.game.logic.database.entity.BaseInfo;
import org.wyf.game.proto.struct.Login.PbRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractGameService<E extends BaseInfo, R extends Repository<E, Integer>> implements GameService<E> {

    private static final Logger log = LoggerFactory.getLogger(AbstractGameService.class);
    @Autowired
    protected R repository;

    protected E entity;

    protected Player player;

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void load() {
        entity = repository.findById(player.getId()).orElse(null);
    }

    @Override
    public void save(boolean cacheEvict) {
        if (entity == null) {
            return;
        }
        save(entity, cacheEvict);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void asyncSave(boolean cacheEvict) {
        if (entity == null) {
            return;
        }
        E copy;
        try {
            copy = (E) entity.clone(); // 复制对象，防止DB线程保存数据的同时，主线程修改数据，造成数据不一致
        } catch (CloneNotSupportedException e) {
            log.error("克隆对象失败，entity: {} {}", entity.getClass().getSimpleName(), JsonUtil.toJson(entity), e);
            copy = entity;
        }
        final E finalCopy = copy;
        player.dbExecute(() -> save(finalCopy, cacheEvict));
    }

    protected void save(E entity, boolean cacheEvict) {
        repository.save(entity, cacheEvict);
    }

    @Override
    public E getEntity() {
        return entity;
    }

    @Override
    public void register(PbRegisterReq registerMsg) {
    }
}
