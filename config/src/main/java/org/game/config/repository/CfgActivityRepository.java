package org.game.config.repository;

import org.game.config.entity.CfgActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgActivityRepository extends JpaRepository<CfgActivity, Integer> {
}