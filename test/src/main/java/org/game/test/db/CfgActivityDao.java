package org.game.test.db;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface CfgActivityDao extends Repository<CfgActivity, String> {

    List<CfgActivity> findAll();

    CfgActivity findById(String id);
}