package org.game.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @Description:
 * @Author: 王叶峰
 * @Date: 2023年05月12日
 **/
@NoRepositoryBean
public interface Dao<T, ID> extends JpaRepository<T, ID> {

    <S extends T> void evict(S entity);

    <S extends T> S saveAndEvict(S entity);

}

