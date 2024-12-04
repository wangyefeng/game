package org.game.logic.data.mysql;

import org.game.logic.data.mongodb.config.CfgService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgActivityService extends CfgService<CfgActivity, CfgActivityDao, String> {
}
