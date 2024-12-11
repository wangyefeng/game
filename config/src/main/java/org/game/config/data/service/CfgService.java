package org.game.config.data.service;

import jakarta.annotation.PostConstruct;
import org.game.config.data.entity.Cfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置表服务基础类
 *
 * @author 王叶峰
 */
public abstract class CfgService<Entity extends Cfg<ID>, Repository extends CrudRepository<Entity, ID>, ID> {

    @Autowired
    protected Repository repository;

    protected Map<ID, Entity> map = new HashMap<>();

    @PostConstruct
    protected void init() {
        repository.findAll().forEach(cfg -> map.put(cfg.getId(), cfg));
    }

    public Entity getCfg(ID id) {
        return map.get(id);
    }

    public Collection<Entity> getAllCfg() {
        return map.values();
    }
}
