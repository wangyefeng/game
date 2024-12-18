package org.game.logic.service;

import org.game.logic.entity.Entity;
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
        entity = repository.findById(player.getId()).orElseThrow();
    }

    @Override
    public void save() {
        repository.save(entity);
    }

    @Override
    public void asyncSave() {
        E copy = (E) entity.clone();// 复制对象，防止DB线程保存数据的同时，主线程修改数据，造成数据不一致
        ThreadPool.getPlayerDBExecutor(player.getId()).execute(() -> repository.save(copy));
    }

    @Override
    public E getEntity() {
        return entity;
    }
}
