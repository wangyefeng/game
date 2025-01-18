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
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class CfgTask implements Cfg<Integer> {

    @Id
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", columnDefinition = "VARCHAR(50) UNSIGNED COMMENT '任务类型'")
    private PlayerEventType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "args", columnDefinition = "JSON COMMENT '任务参数'")
    private String[] args;

    @Column(name = "target", columnDefinition = "BIGINT UNSIGNED COMMENT '任务目标'")
    private long target;

    @Override
    public Integer getId() {
        return id;
    }

    public PlayerEventType getType() {
        return type;
    }

    public String[] getArgs() {
        return args;
    }

    public long getTarget() {
        return target;
    }
}
