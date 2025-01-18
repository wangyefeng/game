package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Valid
public class CfgSimpleTask extends CfgTask {

    @Column(columnDefinition = "INT COMMENT '所属功能ID'")
    private int functionId;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<SimpleItem> rewards;

    public List<SimpleItem> getRewards() {
        return rewards;
    }

    public int getFunctionId() {
        return functionId;
    }
}
