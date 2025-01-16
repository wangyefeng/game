package org.game.config.repository;

import org.game.config.entity.CfgSimpleTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgSimpleTaskRepository extends JpaRepository<CfgSimpleTask, Integer> {
}