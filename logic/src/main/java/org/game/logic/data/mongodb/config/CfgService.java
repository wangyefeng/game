package org.game.logic.data.mongodb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class CfgService<Entity extends Cfg<ID>, Dao extends CrudRepository<Entity, ID>, ID> {

    @Autowired
    protected Dao dao;

    protected Map<ID, Entity> map = new HashMap<>();

    public CfgService() {
    }

    public void init() {
        dao.findAll().forEach(cfg -> map.put(cfg.getId(), cfg));
    }

    public Entity getCfg(ID id) {
        return map.get(id);
    }

    public Collection<Entity> getAllCfg() {
        return map.values();
    }
}
