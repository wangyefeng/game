package org.game.logic.data.config.repository;

import org.game.logic.data.config.entity.CfgFisheries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CfgFisheriesDao extends JpaRepository<CfgFisheries, String> {
}