package org.game.config.data.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.game.config.Configs;
import org.game.config.data.entity.Cfg;
import org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
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

    public void check(Configs config) throws Exception {
        // 创建验证工厂和验证器
        for (Entity entity : map.values()) {
            // 执行验证
            for (ConstraintViolation<Entity> violation : validator.validate(entity)) {
                throw new Exception("配置表：[" + getCfgName(entity) + "] id=[" + entity.getId() + "]出现错误, 字段：[" + getColumnName(entity, violation.getPropertyPath().toString()) + "] 信息：" + violation.getMessage());
            }
        }
        check0(config);
    }

    public String getCfgName(Entity entity) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        MappingMetamodelImpl metaData = (MappingMetamodelImpl) entityManagerFactory.getMetamodel();
        if (!metaData.isEntityClass(entity.getClass())) {
            return entity.getClass().getSimpleName();
        }
        EntityPersister entityPersister = metaData.entityPersister(entity.getClass());
        return entityPersister.getIdentifierTableName();
    }

    public String getColumnName(Entity entity, String field) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        MappingMetamodelImpl metaData = (MappingMetamodelImpl) entityManagerFactory.getMetamodel();
        if (!metaData.isEntityClass(entity.getClass())) {
            return entity.getClass().getSimpleName();
        }
        AbstractEntityPersister persist = (AbstractEntityPersister) metaData.entityPersister(entity.getClass());
        return persist.getPropertyColumnNames(field)[0];
    }

    protected abstract void check0(Configs config) throws Exception;
}
