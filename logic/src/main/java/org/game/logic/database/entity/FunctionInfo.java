package org.game.logic.database.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.Cache;

import java.util.*;
import java.util.Map.Entry;

/**
 * 功能数据
 *
 * @author 王叶峰
 */
@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FunctionInfo extends BaseInfo {

    /**
     * 功能ID集合
     */
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<Integer> functionIds = new HashSet<>();

    /**
     * 周期循环的功能数据
     */
    @Transient
    private Map<Integer, CycleFunction> cycleFunctions = new HashMap<>();

    /**
     * 时间段开启得功能ID集合
     */
    @JdbcTypeCode(SqlTypes.JSON)
    private Set<Integer> timeIntervalIds = new HashSet<>();

    private FunctionInfo() {
        // for JPA
    }

    public FunctionInfo(int playerId) {
        super(playerId);
    }

    public void init(Collection<CycleFunction> cycleFunctions) {
        for (CycleFunction cycleFunction : cycleFunctions) {
            this.cycleFunctions.put(cycleFunction.getId(), cycleFunction);
        }
    }

    public void addFunctionId(int functionId) {
        functionIds.add(functionId);
    }

    public Set<Integer> getFunctionIds() {
        return functionIds;
    }

    public Map<Integer, CycleFunction> getCycleFunctions() {
        return cycleFunctions;
    }

    public Set<Integer> getTimeIntervalIds() {
        return timeIntervalIds;
    }

    @Override
    public FunctionInfo clone() throws CloneNotSupportedException {
        FunctionInfo clone = (FunctionInfo) super.clone();
        clone.functionIds = new HashSet<>(functionIds);
        clone.cycleFunctions = new HashMap<>();
        for (Entry<Integer, CycleFunction> entry : cycleFunctions.entrySet()) {
            clone.cycleFunctions.put(entry.getKey(), entry.getValue().clone());
        }
        clone.timeIntervalIds = new HashSet<>(timeIntervalIds);
        return clone;
    }
}
