package org.game.logic.data.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IDCfgService<Entity extends Cfg<ID>, Dao extends MongoRepository<Entity, ID>, ID> implements CfgService {

    @Autowired
    protected Dao dao;

    protected Map<ID, Entity> map;

    public IDCfgService() {
    }

    @Override
    public void init() {
        List<Entity> cfgs = dao.findAll();
        map = new HashMap<>(cfgs.size());
        for (Entity cfg : cfgs) {
            map.put(cfg.getId(), cfg);
        }
    }

    public Entity getCfg(ID id) {
        return map.get(id);
    }

    public Collection<Entity> getAllCfg() {
        return map.values();
    }

}
