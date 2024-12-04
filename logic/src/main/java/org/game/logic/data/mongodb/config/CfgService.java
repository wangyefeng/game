package org.game.logic.data.mongodb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class CfgService<Entity extends Cfg<ID>, Dao extends CrudRepository<Entity, ID>, ID> {

    @Autowired
    protected Dao dao;

    protected Map<ID, Entity> map;

    public CfgService() {
    }

    public void init(Map<ID, Entity> map) {
        this.map = map;
    }

    @Cacheable(value = "cfg", key = "#tableName")
    public Map<ID, Entity> loadAllCfg(String tableName) {
        Map<ID, Entity> result = new HashMap<>();
        dao.findAll().forEach(cfg -> result.put(cfg.getId(), cfg));
        return result;
    }

    public Entity getCfg(ID id) {
        return map.get(id);
    }

    public Collection<Entity> getAllCfg() {
        return map.values();
    }

    public abstract String getTableName();
}
