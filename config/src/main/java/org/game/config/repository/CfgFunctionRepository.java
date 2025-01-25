package org.game.config.repository;

import org.game.config.entity.CfgFunction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgFunctionRepository extends JpaRepository<CfgFunction, Integer> {
}