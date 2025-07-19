package org.game.config.service;

import org.game.config.ConfigException;
import org.game.config.Configs;
import org.game.config.entity.CfgSimpleTask;
import org.game.config.entity.SimpleItem;
import org.game.config.repository.CfgSimpleTaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CfgSimpleTaskService extends CfgService<CfgSimpleTask, CfgSimpleTaskRepository, Integer> {

    private final Map<Integer, List<CfgSimpleTask>> funcMap = new HashMap<>();

    @Override
    public void init() {
        super.init();
        for (CfgSimpleTask cfgTask : getAllCfg()) {
            funcMap.computeIfAbsent(cfgTask.getFunctionId(), _ -> new ArrayList<>());
            funcMap.get(cfgTask.getFunctionId()).add(cfgTask);
        }
    }

    public List<CfgSimpleTask> getCfgByFuncId(int funcId) {
        return funcMap.get(funcId);
    }

    @Override
    protected void validate0(Configs configsContext) throws ConfigException {
        super.validate0(configsContext);
        CfgItemService cfgItemService = configsContext.getCfgService(CfgItemService.class);
        for (CfgSimpleTask cfgSimpleTask : map.values()) {
            for (SimpleItem reward : cfgSimpleTask.getRewards()) {
                if (!cfgItemService.exists(reward.id())) {
                    throw new ConfigException(tableName, cfgSimpleTask.getId(), "rewards", "奖励配置错误，不存在的奖励物品ID：" + reward.id());
                }
            }

        }
    }
}
