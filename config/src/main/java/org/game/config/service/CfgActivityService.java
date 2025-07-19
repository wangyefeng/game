package org.game.config.service;

import org.game.config.entity.CfgActivity;
import org.game.config.repository.CfgActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class CfgActivityService extends CfgService<CfgActivity, CfgActivityRepository, Integer> {
}
