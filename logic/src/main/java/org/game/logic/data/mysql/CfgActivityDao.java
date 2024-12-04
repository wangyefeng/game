package org.game.logic.data.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CfgActivityDao extends JpaRepository<CfgActivity, String> {
}