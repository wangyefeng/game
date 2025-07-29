package org.game.cache;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
@Transactional
public class BaseDao<T, ID> extends SimpleJpaRepository<T, ID> implements Dao<T, ID> {

    private final Session session;

    private final JpaEntityInformation<T, ?> entityInformation;

    public BaseDao(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        session = em.unwrap(Session.class);
        this.entityInformation = entityInformation;
    }

    @Override
    public <S extends T> void cacheEvict(S entity) {
        session.getFactory().getCache().evict(entity.getClass(), entityInformation.getId(entity));
    }
}
