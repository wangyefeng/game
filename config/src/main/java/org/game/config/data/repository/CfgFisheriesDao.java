package org.game.config.data.repository;

import org.game.config.data.entity.CfgFisheries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgFisheriesDao extends JpaRepository<CfgFisheries, String> {
}