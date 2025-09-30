package org.wyf.game.config.repository;

import org.wyf.game.config.entity.CfgItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfgItemRepository extends JpaRepository<CfgItem, Integer> {
}