package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

@Entity
@Valid
public class CfgActivity implements Cfg<Integer> {

    @Id
    @Column(name = "`id`", columnDefinition = "VARCHAR(32) COMMENT '唯一id'")
    private Integer id;

    @Column(name = "`type`", columnDefinition = "INT COMMENT '活动类型'")
    private int type;

    @Column(name = "`start_time`", columnDefinition = "datetime COMMENT '开始时间'")
    private LocalDateTime startTime;

    @Column(name = "`end_time`", columnDefinition = "datetime COMMENT '结束时间'")
    private LocalDateTime endTime;

    @Override
    public Integer getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
