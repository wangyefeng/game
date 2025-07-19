package org.game.config.service;

import org.game.config.entity.CfgItem;
import org.game.config.repository.CfgItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CfgItemService extends CfgService<CfgItem, CfgItemRepository, Integer> {
}