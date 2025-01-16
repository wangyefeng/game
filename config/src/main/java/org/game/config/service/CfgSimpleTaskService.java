package org.game.config.service;

import org.game.config.entity.CfgSimpleTask;
import org.game.config.repository.CfgSimpleTaskRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgSimpleTaskService extends CfgService<CfgSimpleTask, CfgSimpleTaskRepository, Integer> {

    private Map<Integer, List<CfgSimpleTask>> funcMap = new HashMap<>();

    @Override
    protected void init() {
        super.init();
        for (CfgSimpleTask cfgTask : getAllCfg()) {
            funcMap.computeIfAbsent(cfgTask.getFunctionId(), _ -> new ArrayList<>());
            funcMap.get(cfgTask.getFunctionId()).add(cfgTask);
        }
    }

    public List<CfgSimpleTask> getCfgByFuncId(int funcId) {
        return funcMap.get(funcId);
    }
}
