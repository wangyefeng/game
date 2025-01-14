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
public class TimeIntervalFunctionInfo extends Entity {

    private Set<Integer> functionIds; // 功能ID集合

    public TimeIntervalFunctionInfo(int playerId) {
        super(playerId);
        this.functionIds = new HashSet<>();
    }

    public Set<Integer> getFunctionIds() {
        return functionIds;
    }
}
