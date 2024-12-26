package org.game.logic;

import org.game.logic.player.Player;
import org.game.logic.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractGameService<E extends Entity, R extends MongoRepository<E, Integer>> implements GameService<E> {

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
    public void asyncSave() {
        if (entity == null) {
            return;
        }
        E copy = (E) entity.clone();// 复制对象，防止DB线程保存数据的同时，主线程修改数据，造成数据不一致
        ThreadPool.getPlayerDBExecutor(player.getId()).execute(() -> save(copy));
    }

    private void save(E entity) {
        repository.save(entity);
    }

    @Override
    public E getEntity() {
        return entity;
    }
}
