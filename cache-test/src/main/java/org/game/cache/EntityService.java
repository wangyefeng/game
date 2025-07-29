package org.game.cache;

import org.game.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class EntityService<Entity extends GameEntity, DaoImpl extends Dao<Entity, ID>, ID> {

    private static final Logger log = LoggerFactory.getLogger(EntityService.class);

    @Autowired
    protected DaoImpl dao;

    public void saveAndEvict(Entity entity) {
        try {
            dao.save(entity);
        } catch (Exception e) {
            log.error("数据库保存数据失败，数据类型：{} 数据内容：{}", entity.getClass(), JsonUtil.toJson(entity), e);
        } finally {
            try {
                dao.cacheEvict(entity);
            } catch (Exception e) {
                log.error("");
            }
        }
    }
}