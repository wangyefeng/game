package org.game.logic.data.config.repository;

import org.game.logic.data.config.entity.CfgActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CfgActivityDao extends JpaRepository<CfgActivity, String> {
}