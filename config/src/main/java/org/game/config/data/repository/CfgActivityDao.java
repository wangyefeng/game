package org.game.config.data.repository;

import org.game.config.data.entity.CfgActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgActivityDao extends JpaRepository<CfgActivity, String> {
}