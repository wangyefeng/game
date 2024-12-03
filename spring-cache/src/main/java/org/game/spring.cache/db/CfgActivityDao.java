package org.game.spring.cache.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CfgActivityDao extends JpaRepository<CfgActivity, String> {

    Optional<CfgActivity> findById(String id);
}