package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Valid
@PrimaryKeyJoinColumn(foreignKey = @ForeignKey(name = "none"))
public class CfgActivity extends CfgFunction {

    @Column(name = "`start_time`", columnDefinition = "DATETIME COMMENT '开始时间'")
    @NotNull
    private LocalDateTime startTime;

    @Column(name = "`end_time`", columnDefinition = "DATETIME COMMENT '结束时间'")
    @NotNull
    private LocalDateTime endTime;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
