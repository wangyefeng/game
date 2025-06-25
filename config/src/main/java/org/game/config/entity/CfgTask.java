package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.Valid;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Valid
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CfgTask implements Cfg<Integer> {

    @Id
    @Column(columnDefinition = "INT COMMENT '唯一ID'")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) COMMENT '事件类型'")
    private PlayerEvent event;

    @JdbcTypeCode(SqlTypes.JSON)
    private String[] args;

    @Column(columnDefinition = "BIGINT COMMENT '任务目标'")
    private long target;

    @Override
    public Integer getId() {
        return id;
    }

    public PlayerEvent getEvent() {
        return event;
    }

    public String[] getArgs() {
        return args;
    }

    public long getTarget() {
        return target;
    }
}
