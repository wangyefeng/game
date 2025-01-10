package org.game.config.service;

import org.game.config.Configs;
import org.game.config.entity.CfgActivity;
import org.game.config.repository.CfgActivityRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgActivityService extends CfgService<CfgActivity, CfgActivityRepository, Integer> {

    @Override
    protected void check0(CfgActivity cfgActivity, Configs config) throws Exception {
        Assert.isTrue(cfgActivity.getEndTime().isAfter(cfgActivity.getStartTime()), "活动的结束时间必须大于开始时间");
    }
}
