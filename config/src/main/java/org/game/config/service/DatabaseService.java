package org.game.config.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void lockDatabase() {
        // 执行原生 SQL 语句
        entityManager.createNativeQuery("FLUSH TABLES WITH READ LOCK").executeUpdate();
    }

    @Transactional
    public void unlockDatabase() {
        // 解锁数据库
        entityManager.createNativeQuery("UNLOCK TABLES").executeUpdate();
    }
}
