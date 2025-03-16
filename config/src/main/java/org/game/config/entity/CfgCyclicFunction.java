package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;

import java.time.LocalDate;

@Entity
@Valid
public class CfgCyclicFunction extends CfgFunction {

    @Column(columnDefinition = "INT COMMENT '周期天数'")
    private int cycle;

    @Column(columnDefinition = "INT COMMENT '开启天数'")
    private int openDays;

    @Column(columnDefinition = "DATE COMMENT '开始日期'")
    private LocalDate startDate;

    public int getCycle() {
        return cycle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getOpenDays() {
        return openDays;
    }
}
