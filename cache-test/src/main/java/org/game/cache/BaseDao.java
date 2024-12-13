package org.game.cache;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.EntityInformation;

@NoRepositoryBean
@Transactional
public class BaseDao<T, ID> extends SimpleJpaRepository<T, ID> implements Dao<T, ID> {

    private final Session session;

    private final EntityInformation entityInformation;

    public BaseDao(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        session = em.unwrap(Session.class);
        this.entityInformation = entityInformation;
    }

    @Override
    public <S extends T> void evict(S entity) {
        session.getFactory().getCache().evict(entity.getClass(), entityInformation.getId(entity));
    }

    @Transactional
    @Override
    public <S extends T> S saveAndEvict(S entity) {
        S result = super.saveAndFlush(entity);
        evict(entity);
        return result;
    }
}
