package org.game.config.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.game.config.ConfigException;
import org.game.config.Configs;
import org.game.config.entity.Cfg;
import org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
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

    @Autowired
    protected Validator validator;

    @Autowired
    protected EntityManager entityManager;

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

    public void check(Configs config) throws ConfigException {
        for (Entity entity : map.values()) {
            // 执行验证
            for (ConstraintViolation<Entity> violation : validator.validate(entity)) {
                throw new ConfigException(getCfgName(entity), entity.getId(), getColumnName(entity, violation.getPropertyPath().toString()), violation.getMessage());
            }
            try {
                check0(entity, config);
            } catch (ConfigException e) {
                throw e;
            } catch (Exception e) {
                throw new ConfigException(getCfgName(entity), entity.getId(), null, e.getMessage());
            }
        }
    }

    public String getCfgName(Entity entity) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        MappingMetamodelImpl metaData = (MappingMetamodelImpl) entityManagerFactory.getMetamodel();
        AbstractEntityPersister persist = (AbstractEntityPersister) metaData.entityPersister(entity.getClass());
        return persist.getSubclassTableName(0);
    }

    public String getColumnName(Entity entity, String field) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        MappingMetamodelImpl metaData = (MappingMetamodelImpl) entityManagerFactory.getMetamodel();
        AbstractEntityPersister persist = (AbstractEntityPersister) metaData.entityPersister(entity.getClass());
        return persist.toColumns(field)[0];
    }

    protected abstract void check0(Entity entity, Configs config) throws Exception;
}
