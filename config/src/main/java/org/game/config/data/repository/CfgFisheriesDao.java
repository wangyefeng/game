package org.game.config.data.repository;

import org.game.config.data.entity.CfgFisheries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CfgFisheriesDao extends JpaRepository<CfgFisheries, String> {
}