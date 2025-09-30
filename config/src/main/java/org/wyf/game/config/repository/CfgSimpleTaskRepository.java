package org.wyf.game.config.repository;

import org.wyf.game.config.entity.CfgSimpleTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgSimpleTaskRepository extends JpaRepository<CfgSimpleTask, Integer> {
}