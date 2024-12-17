package org.game.logic.service;

import org.game.common.util.JsonUtil;
import org.game.logic.entity.Entity;
import org.game.logic.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public abstract class AbGameService<E extends Entity, R extends MongoRepository<E, Integer>> implements GameService {

    @Autowired
    protected R repository;

    protected E entity;

    private E copy;

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
    public void copy() {
        copy = (E) entity.clone();
    }

    @Override
    public void save() {
        repository.save(copy);
        copy = null;// 释放内存
    }

    @Override
    public String dataToString() {
        return JsonUtil.toJson(copy);
    }
}
