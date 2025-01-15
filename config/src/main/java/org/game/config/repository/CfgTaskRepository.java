package org.game.config.repository;

import org.game.config.entity.CfgTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgTaskRepository extends JpaRepository<CfgTask, Integer> {
}