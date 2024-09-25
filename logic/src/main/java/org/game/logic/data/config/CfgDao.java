package org.game.logic.data.config;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface CfgDao<T, ID> extends MongoRepository<T, ID> {
	List<T> findAll();
}
