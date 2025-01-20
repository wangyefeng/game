package org.game.config.repository;

import org.game.config.entity.CfgCyclicFunction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgCyclicFunctionRepository extends JpaRepository<CfgCyclicFunction, Integer> {
}