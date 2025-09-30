package org.wyf.game.config.service;

import org.wyf.game.config.entity.CfgItem;
import org.wyf.game.config.repository.CfgItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CfgItemService extends CfgService<CfgItem, CfgItemRepository, Integer> {
}