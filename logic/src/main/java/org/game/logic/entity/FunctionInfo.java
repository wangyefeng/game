package org.game.logic.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 功能数据
 *
 * @author 王叶峰
 */
@Document
public class FunctionInfo extends Entity {

    private Set<Integer> functionIds; // 功能ID集合

    public FunctionInfo(int playerId) {
        super(playerId);
        functionIds = new HashSet<>();
    }

    public void addFunctionId(int functionId) {
        functionIds.add(functionId);
    }

    public Set<Integer> getFunctionIds() {
        return functionIds;
    }
}
