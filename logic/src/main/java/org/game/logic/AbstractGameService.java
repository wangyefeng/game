package org.game.logic;

import org.game.common.util.JsonUtil;
import org.game.logic.entity.Entity;
import org.game.logic.player.Player;
import org.game.logic.thread.ThreadPool;
import org.game.proto.struct.Login.PbRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractGameService<E extends Entity, R extends CrudRepository<E, Integer>> implements GameService<E> {

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
    public void save() {
        if (entity == null) {
            return;
        }
        save(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void asyncSave() {
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
        ThreadPool.getPlayerDBExecutor(player.getId()).execute(() -> save(finalCopy));
    }

    public void save(E entity) {
        repository.save(entity);
    }

    @Override
    public E getEntity() {
        return entity;
    }

    @Override
    public void register(PbRegisterReq registerMsg) {

    }
}
