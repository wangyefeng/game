package org.wyf.game.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wyf.game.config.entity.CfgGlobal;

public interface CfgGlobalRepository extends JpaRepository<CfgGlobal, String> {
}