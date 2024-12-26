package org.game.config.repository;

import org.game.config.entity.CfgFisheries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgFisheriesDao extends JpaRepository<CfgFisheries, String> {
}