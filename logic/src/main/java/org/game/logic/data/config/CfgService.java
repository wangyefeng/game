package org.game.logic.data.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动注入配置服务
 * @param <Entity> 实体类型
 * @param <ID>
 */
public abstract class CfgService<Entity extends Cfg<ID>, Repository extends CrudRepository<Entity, ID>, ID> {

    @Autowired
    protected Repository repository;

    protected Map<ID, Entity> map = new HashMap<>();

    public CfgService() {
    }

    public void init() {
        repository.findAll().forEach(cfg -> map.put(cfg.getId(), cfg));
    }

    public Entity getCfg(ID id) {
        return map.get(id);
    }

    public Collection<Entity> getAllCfg() {
        return map.values();
    }
}
