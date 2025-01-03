package org.game.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyEntityService extends EntityService {

    @Autowired
    private MyEntityDao myEntityDao;

    public void save(MyEntity entity) {
        myEntityDao.save(entity);
    }

    public MyEntity findById(Long id) {
        return myEntityDao.findById(id).orElse(null);
    }
}
