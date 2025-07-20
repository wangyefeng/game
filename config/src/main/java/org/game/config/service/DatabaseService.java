package org.game.config.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseService {

    @PersistenceContext(unitName = "configPersistenceUnit")
    private EntityManager entityManager;

    @Transactional("configTransactionManager")
    public void lockDatabase() {
        // 执行原生 SQL 语句
        entityManager.createNativeQuery("FLUSH TABLES WITH READ LOCK").executeUpdate();
    }

    @Transactional("configTransactionManager")
    public void unlockDatabase() {
        // 解锁数据库
        entityManager.createNativeQuery("UNLOCK TABLES").executeUpdate();
    }
}
