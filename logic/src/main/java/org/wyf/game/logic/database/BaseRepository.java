package org.wyf.game.logic.database;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
@Transactional
public class BaseRepository<T, ID> extends SimpleJpaRepository<T, ID> implements Repository<T, ID> {

    private final Session session;

    private final JpaEntityInformation<T, ?> entityInformation;

    public BaseRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.session = em.unwrap(Session.class);
        this.entityInformation = entityInformation;
    }

    @Override
    public <S extends T> void cacheEvict(S entity) {
        Object id = entityInformation.getId(entity);
        session.getFactory().getCache().evict(entity.getClass(), id);
    }

    @Override
    @Transactional
    public <S extends T> void save(S entity, boolean cacheEvict) {
        super.save(entity);
        if (cacheEvict) {
            cacheEvict(entity);
        }
    }
}
