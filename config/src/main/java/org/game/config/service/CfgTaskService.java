package org.game.config.service;

import org.game.config.Configs;
import org.game.config.entity.CfgTask;
import org.game.config.repository.CfgTaskRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgTaskService extends CfgService<CfgTask, CfgTaskRepository, Integer> {

    private Map<Integer, List<CfgTask>> funcMap = new HashMap<>();

    @Override
    protected void check0(CfgTask cfg, Configs config) throws Exception {
    }

    @Override
    protected void init() {
        super.init();
        for (CfgTask cfgTask : getAllCfg()) {
            funcMap.computeIfAbsent(cfgTask.getFunctionId(), _ -> new ArrayList<>());
            funcMap.get(cfgTask.getFunctionId()).add(cfgTask);
        }
    }

    public List<CfgTask> getCfgByFuncId(int funcId) {
        return funcMap.get(funcId);
    }
}
