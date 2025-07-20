package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
public class CfgSimpleTask extends CfgTask {

    @Column(columnDefinition = "INT COMMENT '所属功能ID'")
    private int functionId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON COMMENT '奖励'")
    private SimpleItem[] rewards;

    public SimpleItem[] getRewards() {
        return rewards;
    }

    public int getFunctionId() {
        return functionId;
    }
}
