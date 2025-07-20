package org.game.logic.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 动态数据源Repository接口
 *
 * @param <T>  数据类型
 * @param <ID> ID类型
 */
@NoRepositoryBean
public interface Repository<T, ID> extends JpaRepository<T, ID> {

    <S extends T> void cacheEvict(S entity);

    <S extends T> void save(S entity, boolean cacheEvict);
}

