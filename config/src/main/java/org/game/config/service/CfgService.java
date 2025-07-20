package org.game.config.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.game.config.ConfigException;
import org.game.config.Configs;
import org.game.config.entity.Cfg;
import org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    @PersistenceContext(unitName = "configPersistenceUnit")
    protected EntityManager entityManager;

    protected String tableName;

    public void init() {
        tableName = getCfgName();
        Scope scope = getClass().getAnnotation(Scope.class);
        if (scope != null && !ConfigurableBeanFactory.SCOPE_PROTOTYPE.equals(scope.value())) {
            throw new IllegalArgumentException(getClass().getName() + " must use prototype scope");
        }
        repository.findAll().forEach(cfg -> map.put(cfg.getId(), cfg));
    }

    public Entity getCfg(ID id) {
        return map.get(id);
    }

    public Collection<Entity> getAllCfg() {
        return map.values();
    }

    public boolean exists(ID id) {
        return map.containsKey(id);
    }

    public void validate(Configs configsContext) throws ConfigException {
        for (Entity entity : map.values()) {
            // 执行验证
            for (ConstraintViolation<Entity> violation : validator.validate(entity)) {
                throw new ConfigException(tableName, entity.getId(), getColumnName(entity, violation.getPropertyPath().toString()), violation.getMessage());
            }
            try {
                entity.validate();
            } catch (ConfigException configException) {
                throw configException;
            } catch (Exception e) {
                throw new ConfigException(tableName, entity.getId(), e);
            }
        }
        try {
            validate0(configsContext);
        } catch (ConfigException configException) {
            throw configException;
        } catch (Exception e) {
            throw new ConfigException(tableName, e);
        }
    }

    private String getCfgName(String entityName) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        MappingMetamodelImpl metaData = (MappingMetamodelImpl) entityManagerFactory.getMetamodel();
        AbstractEntityPersister persist = (AbstractEntityPersister) metaData.entityPersister(entityName);
        return persist.getSubclassTableName(0);
    }

    private String getColumnName(Entity entity, String field) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        MappingMetamodelImpl metaData = (MappingMetamodelImpl) entityManagerFactory.getMetamodel();
        AbstractEntityPersister persist = (AbstractEntityPersister) metaData.entityPersister(entity.getClass());
        return persist.toColumns(field)[0];
    }

    /**
     * 子类可重写此方法，实现自定义验证逻辑
     *
     * @param configsContext 配置上下文
     * @throws ConfigException 验证异常
     */
    protected void validate0(Configs configsContext) throws ConfigException {
        // 子类可重写此方法
    }

    private String getCfgName() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass.getTypeName().startsWith(CfgService.class.getName())) {
            Type[] actualTypes = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            return getCfgName(actualTypes[0].getTypeName());
        }
        throw new IllegalArgumentException("未找到父类泛型参数");
    }

    public String getTableName() {
        return tableName;
    }
}
