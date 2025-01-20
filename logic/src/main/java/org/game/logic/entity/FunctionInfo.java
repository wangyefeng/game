package org.game.logic.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 功能数据
 *
 * @author 王叶峰
 */
@Document
public class FunctionInfo extends Entity {

    /**
     * 功能ID集合
     */
    private Set<Integer> functionIds;

    /**
     * 周期循环的功能数据
     */
    private Map<Integer, DbCycleFunction> cycleFunctions;

    public FunctionInfo(int playerId) {
        super(playerId);
        functionIds = new HashSet<>();
        cycleFunctions = new HashMap<>();
    }

    public void addFunctionId(int functionId) {
        functionIds.add(functionId);
    }

    public Set<Integer> getFunctionIds() {
        return functionIds;
    }

    public Map<Integer, DbCycleFunction> getCycleFunctions() {
        return cycleFunctions;
    }

    @Override
    public FunctionInfo clone() throws CloneNotSupportedException {
        FunctionInfo clone = (FunctionInfo) super.clone();
        clone.functionIds = new HashSet<>(functionIds);
        clone.cycleFunctions = new HashMap<>();
        for (Entry<Integer, DbCycleFunction> entry : cycleFunctions.entrySet()) {
            clone.cycleFunctions.put(entry.getKey(), entry.getValue().clone());
        }
        return clone;
    }
}
