package org.game.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyEntityService {

    @Autowired
    private MyEntityDao myEntityDao;

    public void save(MyEntity entity) {
        myEntityDao.save(entity);
    }

    public MyEntity findById(Long id) {
        return myEntityDao.findById(id).orElse(null);
    }

    public MyEntity saveAndEvict(MyEntity entity) {
        myEntityDao.save(entity);
        myEntityDao.evict(entity);
        return entity;
    }
}
