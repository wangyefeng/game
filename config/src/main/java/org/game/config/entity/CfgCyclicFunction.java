package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.Valid;

import java.time.LocalDate;

@Entity
@Valid
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "none"))
public class CfgCyclicFunction extends CfgFunction {

    @Column(name = "cycle", columnDefinition = "INT COMMENT '周期天数'")
    private int cycle;

    @Column(name = "base_date", columnDefinition = "DATE COMMENT '基准日期'")
    private LocalDate baseDate;

    public int getCycle() {
        return cycle;
    }

    public LocalDate getBaseDate() {
        return baseDate;
    }
}
