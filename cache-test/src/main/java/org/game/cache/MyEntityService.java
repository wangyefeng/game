package org.game.cache;

import org.springframework.stereotype.Service;

@Service
public class MyEntityService extends EntityService<MyEntity, MyEntityDao, Long> {

    public void save(MyEntity entity) {
        dao.save(entity);
    }

    public MyEntity findById(Long id) {
        return dao.findById(id).orElse(null);
    }
}
