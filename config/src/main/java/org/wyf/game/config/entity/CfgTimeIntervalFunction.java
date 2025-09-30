package org.wyf.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Valid
public abstract class CfgTimeIntervalFunction extends CfgFunction {

    @Column(columnDefinition = "DATETIME COMMENT '开始时间'")
    @NotNull
    private LocalDateTime startTime;

    @Column(columnDefinition = "DATETIME COMMENT '结束时间'")
    @NotNull
    private LocalDateTime endTime;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
