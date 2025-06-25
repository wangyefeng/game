package org.game.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.Valid;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Valid
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class CfgFunction implements Cfg<Integer> {

    @Id
    @Column(columnDefinition = "INT COMMENT '唯一ID'")
    private int id;

    @JdbcTypeCode(SqlTypes.JSON)
    private ModuleEnum[] modules;

    @Override
    public Integer getId() {
        return id;
    }

    public ModuleEnum[] getModules() {
        return modules;
    }
}
